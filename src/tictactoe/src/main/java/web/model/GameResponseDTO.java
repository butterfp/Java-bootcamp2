package web.model;

import domain.model.GameBoard;

import java.util.UUID;

public record GameResponseDTO(UUID gameId, GameBoard board, String status) {
}