package datasource.model;

import java.util.UUID;

public record GameEntity(UUID id, int[][] board) {
}