package domain.service;

import domain.model.GameBoard;
import domain.model.GameSession;
import domain.model.PlayerType;

import java.util.List;
import java.util.UUID;

/**
 * Файл: domain/service/TicTacToeService.java
 *
 * Назначение файла: контракт бизнес-логики игры. Веб-слой вызывает только эти методы,
 * не зная, где лежат данные (PostgreSQL) и как именно считается ход компьютера.
 *
 * Методы сгруппированы по заданиям:
 * - базовые (задание 2 курса): создать игру, получить игру, проверить/сделать ход, статус;
 * - задание 3: игра с пользователем или компьютером, состояния партии,
 *   список доступных игр, присоединение второго игрока.
 */
public interface TicTacToeService {

    /**
     * Создать новую партию (задание 3).
     *
     * @param opponentType с кем играем: USER (ждём второго человека) или AI (компьютер займёт O)
     * @param playerId     UUID автора партии (он играет крестиками)
     * @return созданная игровая сессия в состоянии WAITING или X_TURN
     */
    GameSession createGame(PlayerType opponentType, UUID playerId);

    /**
     * Получить партию по идентификатору (задание 3, endpoint GET /game/{id}).
     *
     * @param gameId UUID игры
     * @return игровая сессия
     * @throws IllegalArgumentException если игра не найдена
     */
    GameSession getGameById(UUID gameId);

    /**
     * Список партий, ожидающих второго игрока (задание 3, endpoint GET /game/available).
     *
     * @return доступные текущие игры
     */
    List<GameSession> getAvailableGames();

    /**
     * Присоединиться к существующей партии (задание 3, endpoint POST /game/{id}/join).
     * Гость получает нолики, партия переходит из WAITING в активную фазу.
     *
     * @param gameId     UUID игры
     * @param playerId   UUID присоединяющегося пользователя
     * @return обновлённая игровая сессия
     */
    GameSession joinGame(UUID gameId, UUID playerId);

    /**
     * Проверить корректность хода относительно сохранённой партии
     * (принадлежность значка, очерёдность, легальность клетки).
     *
     * @param gameId       UUID игры
     * @param playerId     UUID сделавшего ход
     * @param updatedBoard поле после хода клиента
     * @return true, если ход допустим
     */
    boolean validateMove(UUID gameId, UUID playerId, GameBoard updatedBoard);

    /**
     * Применить ход человека и, если нужно, ответный ход компьютера (задание 3).
     * После метода состояние партии пересчитано: X_TURN/O_TURN/DRAW/WIN_X/WIN_O.
     *
     * @param gameId       UUID игры
     * @param playerId     UUID ходящего
     * @param updatedBoard поле после хода клиента
     * @return обновлённая игровая сессия
     */
    GameSession makeMove(UUID gameId, UUID playerId, GameBoard updatedBoard);

    /**
     * Текстовый статус партии для ответа клиенту (берётся из GameState).
     *
     * @param game игровая сессия
     * @return человекочитаемое описание состояния
     */
    String getGameStatus(GameSession game);
}
