/**
 * REST-контроллер для управления игровым процессом «Крестики-нолики» (/game).
 *
 * Назначение:
 * 1. Обрабатывает входящие HTTP-запросы от клиентов (создание игры, совершение ходов).
 * 2. Обеспечивает безопасность: использует SecurityContextHolder для автоматического
 *    извлечения ID текущего авторизованного пользователя из JWT-токена.
 * 3. Делегирует бизнес-логику игровому сервису (TicTacToeService).
 */
package controller;

import domain.model.GameBoard;
import domain.model.GameSession;
import domain.service.TicTacToeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {

    private final TicTacToeService gameService;

    // Внедряем игровой сервис через конструктор
    public GameController(TicTacToeService gameService) {
        this.gameService = gameService;
    }

    // Создание новой игры
    @PostMapping("/create")
    public ResponseEntity<GameSession> createGame() {
        // 1. Узнаем ID текущего игрока из JWT-токена
        UUID currentUserId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 2. Создаем сессию игры через сервис
        GameSession session = gameService.createGame();

        // Возвращаем созданную игровую сессию клиенту
        return ResponseEntity.ok(session);
    }

    // Совершение хода
    @PostMapping("/move")
    public ResponseEntity<String> makeMove(@RequestParam UUID gameId, @RequestParam int x, @RequestParam int y) {
        // 1. Узнаем, кто ходит
        UUID currentUserId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 2. Получаем игру по ID
        GameSession session = gameService.getGameById(gameId);

        // 3. Здесь будет проверка хода и обновление доски через методы твоего сервиса
        // Например: gameService.updateBoard(...)

        return ResponseEntity.ok("Игрок " + currentUserId + " сделал ход в игру " + gameId + " на клетку [" + x + ", " + y + "]");
    }
}