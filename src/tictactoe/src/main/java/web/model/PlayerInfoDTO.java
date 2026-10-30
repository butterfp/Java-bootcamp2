package web.model;

import java.util.UUID;

/**
 * Файл: web/model/PlayerInfoDTO.java
 *
 * Назначение файла (задание 3): компактное описание участника игры для JSON-ответа.
 * Тип говорит, человек это или компьютер; id — UUID человека (null для AI).
 */
public record PlayerInfoDTO(String type, UUID id) {
}
