package domain.model;

import java.util.UUID;

public record GameSession(UUID id, GameBoard board) {
}