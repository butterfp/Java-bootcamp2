package web.model;

import domain.model.GameBoard;

import java.util.UUID;

/**
 * Файл: web/model/GameResponseDTO.java
 *
 * Назначение файла: ответ веб-слоя с полным описанием текущей партии.
 * Собирается WebMapper из доменной GameSession.
 *
 * Поля (задание 3):
 * - gameId        — идентификатор партии;
 * - xPlayer/oPlayer — участники и их значки (X всегда ходит первым);
 * - board         — поле 3x3 (0 пусто, 1 крестики, 2 нолики);
 * - state         — WAITING / X_TURN / O_TURN / DRAW / WIN_X / WIN_O;
 * - currentPlayerId — чей сейчас ход (UUID игрока с ходом);
 * - winnerId      — UUID победителя (для состояний WIN_X/WIN_O);
 * - status        — человекочитаемое описание состояния.
 */
public record GameResponseDTO(
        UUID gameId,
        PlayerInfoDTO xPlayer,
        PlayerInfoDTO oPlayer,
        GameBoard board,
        String state,
        UUID currentPlayerId,
        UUID winnerId,
        String status
) {
}
