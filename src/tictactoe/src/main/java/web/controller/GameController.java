package web.controller;

import domain.model.GameSession;
import domain.model.PlayerType;
import domain.service.TicTacToeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import web.mapper.WebMapper;
import web.model.GameRequestDTO;
import web.model.GameResponseDTO;

import java.util.List;
import java.util.UUID;

/**
 * Файл: web/controller/GameController.java
 *
 * Назначение файла: контроллер игр (задание 3). Реализует все endpoint'ы,
 * которые требует задание:
 *  - POST /game?opponent=USER|AI   — создание новой игры с пользователем или компьютером;
 *  - GET  /game/available          — получение списка доступных (ожидающих игрока) игр;
 *  - POST /game/{id}/join          — присоединение пользователя к игре;
 *  - POST /game/{id}               — обновление текущей игры (ход человека + ответ компьютера);
 *  - GET  /game/{id}               — получение текущей игры.
 *
 * Все адреса защищены (см. security/SecurityConfig): AuthFilter после проверки
 * заголовка base64(login:password) кладёт UUID пользователя в SecurityContext,
 * а контроллер получает его через @AuthenticationPrincipal — без ручного разбора
 * заголовков и без доверия идентификатору из тела запроса.
 */
@RestController
@RequestMapping("/game")
public class GameController {

    /** Бизнес-логика игры: создание, подключение, ходы, состояния партии. */
    private final TicTacToeService ticTacToeService;

    /** Конвертер доменной GameSession -> JSON-ответ GameResponseDTO. */
    private final WebMapper webMapper;

    /**
     * Конструкторное внедрение зависимостей (Spring передаст готовые бины).
     *
     * @param ticTacToeService сервис игры
     * @param webMapper        маппер ответов
     */
    public GameController(TicTacToeService ticTacToeService, WebMapper webMapper) {
        this.ticTacToeService = ticTacToeService;
        this.webMapper = webMapper;
    }

    /**
     * Endpoint создания новой игры (задание 3: «с пользователем или компьютером»).
     *
     * Примеры:
     *   POST /game?opponent=AI   — игра против компьютера (состояние сразу X_TURN);
     *   POST /game?opponent=USER — игра будет ждать второго человека (состояние WAITING).
     * Создатель партии всегда играет крестиками (X).
     *
     * @param opponentType тип соперника из query-параметра (по умолчанию USER)
     * @param userId       UUID текущего авторизованного пользователя
     * @return описание созданной партии, код 201 Created
     */
    @PostMapping
    public ResponseEntity<GameResponseDTO> createGame(
            @RequestParam(name = "opponent", defaultValue = "USER") PlayerType opponentType,
            @AuthenticationPrincipal UUID userId) {
        GameSession game = ticTacToeService.createGame(opponentType, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(game));
    }

    /**
     * Endpoint получения доступных текущих игр (задание 3).
     * Возвращает партии, которые ещё ждут второго игрока (состояние WAITING).
     *
     * @return список описаний доступных партий
     */
    @GetMapping("/available")
    public List<GameResponseDTO> getAvailableGames() {
        return ticTacToeService.getAvailableGames().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Endpoint присоединения пользователя к игре (задание 3).
     * Гость занимает сторону O (нолики); партия переходит из WAITING в активную фазу.
     * Ошибки сервиса (игра занята/завершена/не найдена) превращает в 4xx
     * обработчик web/exception/GlobalExceptionHandler.
     *
     * @param id     UUID игры из пути
     * @param userId UUID текущего авторизованного пользователя
     * @return обновлённое описание партии
     */
    @PostMapping("/{id}/join")
    public GameResponseDTO joinGame(@PathVariable("id") UUID id,
                                    @AuthenticationPrincipal UUID userId) {
        return toDto(ticTacToeService.joinGame(id, userId));
    }

    /**
     * Endpoint обновления текущей игры (ход) — улучшен с учётом игры
     * с пользователем или компьютером (задание 3).
     *
     * Клиент присылает поле ПОСЛЕ своего хода. Сервис:
     *  1) проверяет принадлежность хода и легальность клетки (validateMove);
     *  2) сохраняет ход;
     *  3) если второй участник — компьютер, автоматически добавляет его ответный ход
     *     (алгоритм minimax) и пересчитывает состояние партии.
     *
     * @param id     UUID игры из пути
     * @param dto    тело запроса: {"board": [[1,0,0],[0,0,0],[0,0,0]]}
     * @param userId UUID автора хода
     * @return актуальное состояние партии (в т.ч. после хода компьютера)
     */
    @PostMapping("/{id}")
    public GameResponseDTO updateGame(@PathVariable("id") UUID id,
                                      @RequestBody GameRequestDTO dto,
                                      @AuthenticationPrincipal UUID userId) {
        // Проверка корректности: при неверном ходе бросается IllegalArgumentException -> 400
        if (!ticTacToeService.validateMove(id, userId, dto.board())) {
            throw new IllegalArgumentException("Ход некорректен или сделан не в свою очередь");
        }
        GameSession game = ticTacToeService.makeMove(id, userId, dto.board());
        return toDto(game);
    }

    /**
     * Endpoint получения текущей игры (задание 3).
     *
     * @param id UUID игры из пути
     * @return полное описание партии: участники, значки, поле, чей ход, состояние, победитель
     */
    @GetMapping("/{id}")
    public GameResponseDTO getGame(@PathVariable("id") UUID id) {
        return toDto(ticTacToeService.getGameById(id));
    }

    /**
     * Служебный метод: доменная партия -> DTO ответа (статус берётся из сервиса,
     * где он строится на основе GameState).
     *
     * @param game игровая сессия
     * @return DTO для JSON-сериализации
     */
    private GameResponseDTO toDto(GameSession game) {
        return webMapper.toResponse(game, ticTacToeService.getGameStatus(game));
    }
}
