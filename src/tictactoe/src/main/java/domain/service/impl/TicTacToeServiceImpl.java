package domain.service.impl;

import domain.model.GameBoard;
import domain.model.GameSession;
import domain.model.GameState;
import domain.model.PlayerType;
import domain.repository.GameRepository;
import domain.service.TicTacToeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Файл: domain/service/impl/TicTacToeServiceImpl.java
 *
 * Назначение файла: реализация бизнес-логики «Крестики-нолики», включая задание 3
 * (игра с пользователем или компьютером, состояния партии, алгоритм minimax).
 *
 * Ключевые правила реализации:
 * - GameSession — неизменяемый record, поэтому любое изменение партии = создание нового
 *   объекта и сохранение через GameRepository (данные живут в PostgreSQL);
 * - состояние партии хранится явно (GameState), а не вычисляется каждый раз заново —
 *   это и есть «улучшение алгоритма определения окончания игры с использованием состояний»;
 * - computer (AI) отвечает ходом minimax только когда его очередь и партия активна.
 */
@Service
public class TicTacToeServiceImpl implements TicTacToeService {

    private final GameRepository gameRepository;

    /**
     * Конструкторное внедрение контракта репозитория (реализация — в datasource).
     *
     * @param gameRepository хранилище игровых сессий
     */
    public TicTacToeServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    /**
     * Создание новой игры (задание 3, endpoint POST /game/create).
     * Автор партии всегда получает крестики и ходит первым.
     * - opponentType = AI  -> нолики сразу занимает компьютер, партия стартует (X_TURN);
     * - opponentType = USER-> вторая сторона пуста, состояние WAITING (ждём соперника).
     */
    @Override
    public GameSession createGame(PlayerType opponentType, UUID playerId) {
        if (playerId == null) {
            throw new IllegalArgumentException("Игру может создать только авторизованный пользователь");
        }
        GameSession game;
        if (opponentType == PlayerType.AI) {
            // Играем с компьютером: второй участник известен сразу
            game = new GameSession(UUID.randomUUID(), new GameBoard(),
                    PlayerType.USER, playerId,
                    PlayerType.AI, null,
                    GameState.X_TURN, null);
        } else {
            // Играем с человеком: ждём присоединения (состояние WAITING)
            game = new GameSession(UUID.randomUUID(), new GameBoard(),
                    PlayerType.USER, playerId,
                    null, null,
                    GameState.WAITING, null);
        }
        gameRepository.save(game);
        return game;
    }

    /**
     * Получение игры по UUID (задание 3, endpoint GET /game/{id}).
     *
     * @throws IllegalArgumentException если партии нет в базе
     */
    @Override
    public GameSession getGameById(UUID gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Игра с ID " + gameId + " не найдена"));
    }

    /** Список партий, ожидающих второго игрока (endpoint GET /game/available). */
    @Override
    public List<GameSession> getAvailableGames() {
        return gameRepository.findWaitingGames();
    }

    /**
     * Присоединение пользователя к игре (задание 3, endpoint POST /game/{id}/join).
     * Гость получает нолики; партия переходит из WAITING в X_TURN (ходит создатель — крестики).
     *
     * Ограничения: подключаться можно только к ожидающей партии, нельзя занять место
     * уже участвующего игрока и нельзя заходить в собственную игру.
     */
    @Override
    public GameSession joinGame(UUID gameId, UUID playerId) {
        GameSession game = getGameById(gameId);

        if (game.state() != GameState.WAITING) {
            throw new IllegalStateException("В игру уже кто-то подключён или партия начата");
        }
        if (playerId == null || playerId.equals(game.xPlayerId())) {
            throw new IllegalArgumentException("Нельзя присоединиться к собственной игре");
        }

        GameSession joined = new GameSession(game.id(), game.board(),
                game.xPlayerType(), game.xPlayerId(),
                PlayerType.USER, playerId,
                GameState.X_TURN, null);
        gameRepository.save(joined);
        return joined;
    }

    /**
     * Валидация хода (задание 3): проверяются три вещи
     * 1) партия активна (не WAITING и не завершена);
     * 2) ходит именно та сторона, чей черёд (по значку и UUID);
     * 3) переход доски линеен (одно новое значение, очерёдность соблюдена).
     */
    @Override
    public boolean validateMove(UUID gameId, UUID playerId, GameBoard updatedBoard) {
        GameSession game = getGameById(gameId);

        if (!game.state().isActive()) {
            return false; // партия не идёт: ожидание либо уже окончена
        }
        if (updatedBoard == null) {
            return false;
        }
        int playerMark = game.markOf(playerId);
        if (playerMark < 0) {
            return false; // пользователь не участник этой партии
        }
        int expectedMark = game.state() == GameState.X_TURN ? GameBoard.X : GameBoard.O;
        if (playerMark != expectedMark) {
            return false; // ход не той стороны
        }
        return game.board().isValidMove(updatedBoard);
    }

    /**
     * Применение хода (задание 3, улучшенный endpoint обновления игры).
     *
     * Шаги:
     * 1. повторная проверка хода (защита от гонки между validate и update);
     * 2. пересчёт состояния после хода человека (ничья/победа/ход соперника);
     * 3. сохранение промежуточного состояния;
     * 4. если партия против компьютера и её черёд — автоматический ответный ход minimax
     *    и ещё один пересчёт состояния;
     * 5. возврат финального снимка партии.
     */
    @Override
    public GameSession makeMove(UUID gameId, UUID playerId, GameBoard updatedBoard) {
        if (!validateMove(gameId, playerId, updatedBoard)) {
            throw new IllegalArgumentException("Некорректный ход");
        }
        GameSession game = getGameById(gameId);

        // Ход человека применяем и пересчитываем состояние партии
        GameSession afterPlayer = withState(game, updatedBoard);
        gameRepository.save(afterPlayer);

        // Если игра против компьютера и партия ещё идёт — компьютер ходит за свою сторону
        GameSession current = afterPlayer;
        if (current.isAgainstComputer() && !current.state().isFinished()) {
            int aiMark = current.state() == GameState.X_TURN ? GameBoard.X : GameBoard.O;
            GameBoard boardWithAiMove = bestMove(current.board(), aiMark);
            current = withState(current, boardWithAiMove);
            gameRepository.save(current);
        }
        return current;
    }

    /**
     * Человекочитаемый статус для ответа клиенту. Строится из GameState —
     * «победа игрока с UUID» формулируется через winnerId партии.
     */
    @Override
    public String getGameStatus(GameSession game) {
        return switch (game.state()) {
            case WAITING -> "Ожидание игроков";
            case X_TURN -> "Ход игрока с UUID " + game.xPlayerId() + " (X)";
            case O_TURN -> "Ход игрока с UUID " + game.oPlayerId() + " (O)";
            case DRAW -> "Ничья";
            case WIN_X -> "Победа игрока с UUID " + game.winnerId();
            case WIN_O -> "Победа игрока с UUID " + game.winnerId();
        };
    }

    /**
     * Служебный метод: создаёт новый снимок партии с применённой доской и ПЕРЕСЧИТАННЫМ
     * состоянием. Именно здесь работает «улучшенный алгоритм определения окончания игры»:
     * сначала проверяется победная линия сделавшего ход, затем заполненность поля (ничья),
     * иначе передаётся черёд следующей стороне.
     *
     * @param game      текущий снимок партии
     * @param newBoard  доска после сделанного хода
     * @return новая GameSession с актуальным state/winnerId
     */
    private GameSession withState(GameSession game, GameBoard newBoard) {
        int movedMark = game.state() == GameState.X_TURN ? GameBoard.X : GameBoard.O;

        GameState newState;
        UUID winnerId = null;

        if (newBoard.isWinner(movedMark)) {
            // Победа: состояние WIN_X/WIN_O + UUID победителя (задание 3)
            newState = movedMark == GameBoard.X ? GameState.WIN_X : GameState.WIN_O;
            winnerId = movedMark == GameBoard.X ? game.xPlayerId() : game.oPlayerId();
        } else if (!newBoard.hasEmptyCell()) {
            newState = GameState.DRAW; // Ничья: свободных клеток нет, линии нет
        } else {
            newState = movedMark == GameBoard.X ? GameState.O_TURN : GameState.X_TURN;
        }
        return new GameSession(game.id(), newBoard,
                game.xPlayerType(), game.xPlayerId(),
                game.oPlayerType(), game.oPlayerId(),
                newState, winnerId);
    }

    /**
     * Выбор лучшего хода компьютера алгоритмом minimax (значение бесконечность для
     * текущего игрока означает гарантированную победу при дальнейшей идеальной игре).
     *
     * @param board       текущее поле
     * @param aiMark      значок, которым ходит компьютер (1 или 2)
     * @return поле с добавленным ходом компьютера
     */
    private GameBoard bestMove(GameBoard board, int aiMark) {
        int bestScore = Integer.MIN_VALUE;
        int bestRow = -1;
        int bestCol = -1;
        int[][] cells = board.board();

        for (int i = 0; i < GameBoard.SIZE; i++) {
            for (int j = 0; j < GameBoard.SIZE; j++) {
                if (cells[i][j] == GameBoard.EMPTY) {
                    cells[i][j] = aiMark;                       // пробуем ход
                    int score = minimax(board, 0, false, aiMark); // оцениваем позицию соперника
                    cells[i][j] = GameBoard.EMPTY;               // откатываем пробный ход
                    if (score > bestScore) {
                        bestScore = score;
                        bestRow = i;
                        bestCol = j;
                    }
                }
            }
        }
        if (bestRow >= 0) {
            cells[bestRow][bestCol] = aiMark;
        }
        return new GameBoard(cells);
    }

    /**
     * Рекурсивный minimax: деревья возможных продолжений перебираются до конца партии,
     * оценка листа = победа/поражение/ничья. isMaximizing=true — ход того, ради кого
     * ищем лучший вариант (компьютера), false — ход соперника, который минимизирует оценку.
     *
     * @param board       рассматриваемое поле
     * @param depth       глубина рекурсии (используется для приоритета быстрых побед)
     * @param isMaximizing чей ход моделируется на этом уровне
     * @param aiMark      значок компьютера
     * @return оценка позиции
     */
    private int minimax(GameBoard board, int depth, boolean isMaximizing, int aiMark) {
        int opponentMark = GameBoard.opponentOf(aiMark);

        // Терминальные узлы: победа/поражение/ничья — состояние определяем по доске
        if (board.isWinner(aiMark)) {
            return 10 - depth;      // чем быстрее победа, тем она ценнее
        }
        if (board.isWinner(opponentMark)) {
            return depth - 10;      // чем позже поражение, тем оно «менее плохое»
        }
        if (!board.hasEmptyCell()) {
            return 0;               // ничья
        }

        int[][] cells = board.board();
        int best = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < GameBoard.SIZE; i++) {
            for (int j = 0; j < GameBoard.SIZE; j++) {
                if (cells[i][j] == GameBoard.EMPTY) {
                    cells[i][j] = isMaximizing ? aiMark : opponentMark;
                    int score = minimax(board, depth + 1, !isMaximizing, aiMark);
                    cells[i][j] = GameBoard.EMPTY;
                    best = isMaximizing ? Math.max(best, score) : Math.min(best, score);
                }
            }
        }
        return best;
    }
}
