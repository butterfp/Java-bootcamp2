package web.model;

import java.util.UUID;

/**
 * Файл: web/model/UserInfoDTO.java
 *
 * Назначение файла (задание 3): ответ endpoint'а «получить информацию о пользователе
 * по UUID». Пароль в DTO отсутствует намеренно — клиенту он не возвращается никогда.
 */
public record UserInfoDTO(UUID id, String login) {
}
