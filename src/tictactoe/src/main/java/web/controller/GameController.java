package web.controller;

import domain.model.GameSession;
import domain.service.TicTacToeService;
import web.mapper.WebMapper;
import web.model.GameRequestDTO;
import web.model.GameResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {

    private final TicTacToeService gameService;
    private final WebMapper webMapper;

    public GameController(TicTacToeService gameService, WebMapper webMapper) {
        this.gameService = gameService;
        this.webMapper = webMapper;
    }

    @PostMapping
    public ResponseEntity<GameResponseDTO> startGame() {
        GameSession newGame = gameService.createGame();
        return ResponseEntity.ok(webMapper.toResponse(newGame, "PLAYING"));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> makeMove(@PathVariable UUID id, @RequestBody GameRequestDTO request) {
        if (!gameService.validateBoard(id, request.board())) {
            return ResponseEntity.badRequest().body("Некорректный ход.");
        }

        gameService.updateBoard(id, request.board());

        String statusAfterPlayer = gameService.getGameStatus(request.board());
        if (!statusAfterPlayer.equals("PLAYING")) {
            return ResponseEntity.ok(webMapper.toResponse(gameService.getGameById(id), statusAfterPlayer));
        }

        gameService.getNextMove(id);

        GameSession finalState = gameService.getGameById(id);
        String finalStatus = gameService.getGameStatus(finalState.board());

        return ResponseEntity.ok(webMapper.toResponse(finalState, finalStatus));
    }
}