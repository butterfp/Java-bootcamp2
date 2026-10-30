package web.model;

import domain.model.GameBoard;

/**
 * Файл: web/model/GameRequestDTO.java
 *
 * Назначение файла: тело запроса на обновление игры (ход).
 * Клиент присылает поле после СВОЕГО хода:
 * {"board": [[1,0,0],[0,2,0],[0,0,0]]}
 *
 * Сервис проверит переход «старое поле -> новое» (GameBoard.isValidMove) и,
 * если игра идёт против компьютера, добавит ответный ход сам.
 */
public record GameRequestDTO(GameBoard board) {
}
