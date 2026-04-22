package domain.model;

import java.util.Arrays;

public record GameBoard(int[][] board) {
    public GameBoard() {
        this(new int[3][3]);
    }

    @Override
    public String toString() {
        return Arrays.deepToString(board);
    }
}