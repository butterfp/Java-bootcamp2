package domain.service.impl;

import datasource.repository.GameRepository;
import domain.model.GameBoard;
import domain.model.GameSession;
import domain.service.TicTacToeService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TicTacToeServiceImpl implements TicTacToeService {

    private final GameRepository gameRepository;

    public TicTacToeServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public GameSession createGame() {
        GameSession newGame = new GameSession(UUID.randomUUID(), new GameBoard());
        gameRepository.save(newGame);
        return newGame;
    }

    @Override
    public GameSession getGameById(UUID id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Игра с ID " + id + " не найдена!"));
    }

    @Override
    public void updateBoard(UUID id, GameBoard board) {
        GameSession session = getGameById(id);
        GameSession updatedSession = new GameSession(session.id(), board);
        gameRepository.save(updatedSession);
    }

    @Override
    public boolean validateBoard(UUID gameId, GameBoard updatedBoard) {
        GameSession originalGame = getGameById(gameId);
        GameBoard originalBoard = originalGame.board();

        int originalX = 0, originalO = 0;
        int updatedX = 0, updatedO = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int oldVal = originalBoard.board()[i][j];
                int newVal = updatedBoard.board()[i][j];

                if (oldVal == 1) originalX++;
                if (oldVal == 2) originalO++;
                if (newVal == 1) updatedX++;
                if (newVal == 2) updatedO++;

                if (oldVal != 0 && oldVal != newVal) return false;
            }
        }
        return (updatedX == originalX + 1) && (updatedO == originalO);
    }

    @Override
    public void getNextMove(UUID gameId) {
        GameSession game = getGameById(gameId);
        GameBoard board = game.board();

        if (isGameOver(board)) return;

        int bestScore = Integer.MIN_VALUE;
        int[] move = {-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.board()[i][j] == 0) {
                    board.board()[i][j] = 2;
                    int score = minimax(board, 0, false);
                    board.board()[i][j] = 0;

                    if (score > bestScore) {
                        bestScore = score;
                        move[0] = i;
                        move[1] = j;
                    }
                }
            }
        }

        if (move[0] != -1) {
            board.board()[move[0]][move[1]] = 2;
            updateBoard(gameId, board);
        }
    }

    private int minimax(GameBoard board, int depth, boolean isMaximizing) {
        int score = evaluate(board);
        if (score != 0 || isGameOver(board)) return score;

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board.board()[i][j] == 0) {
                        board.board()[i][j] = 2;
                        best = Math.max(best, minimax(board, depth + 1, false));
                        board.board()[i][j] = 0;
                    }
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board.board()[i][j] == 0) {
                        board.board()[i][j] = 1;
                        best = Math.min(best, minimax(board, depth + 1, true));
                        board.board()[i][j] = 0;
                    }
                }
            }
            return best;
        }
    }

    @Override
    public String getGameStatus(GameBoard board) {
        if (checkWin(board.board(), 1)) return "PLAYER_WON (X)";
        if (checkWin(board.board(), 2)) return "AI_WON (O)";

        boolean hasEmpty = false;
        for (int[] row : board.board()) {
            for (int cell : row) {
                if (cell == 0) {
                    hasEmpty = true;
                    break;
                }
            }
        }
        return hasEmpty ? "PLAYING" : "DRAW";
    }

    private boolean isGameOver(GameBoard board) {
        return !getGameStatus(board).equals("PLAYING");
    }

    private int evaluate(GameBoard board) {
        if (checkWin(board.board(), 2)) return 10;
        if (checkWin(board.board(), 1)) return -10;
        return 0;
    }

    private boolean checkWin(int[][] b, int p) {
        for (int i = 0; i < 3; i++) {
            if (b[i][0] == p && b[i][1] == p && b[i][2] == p) return true;
            if (b[0][i] == p && b[1][i] == p && b[2][i] == p) return true;
        }
        return (b[0][0] == p && b[1][1] == p && b[2][2] == p) ||
                (b[0][2] == p && b[1][1] == p && b[2][0] == p);
    }
}