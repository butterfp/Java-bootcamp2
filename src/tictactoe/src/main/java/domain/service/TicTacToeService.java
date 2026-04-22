package domain.service;

import domain.model.GameBoard;
import domain.model.GameSession;
import java.util.UUID;

public interface TicTacToeService {

    void updateBoard(UUID id, GameBoard board);

    void getNextMove(UUID gameId);

    boolean validateBoard(UUID gameId, GameBoard updatedBoard);

    String getGameStatus(GameBoard board);

    GameSession getGameById(UUID id);

    GameSession createGame();
}