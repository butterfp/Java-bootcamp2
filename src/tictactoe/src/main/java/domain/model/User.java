package domain.model;

import java.util.UUID;

/**
 * Файл: domain/model/User.java
 *
 * Назначение файла: доменная модель пользователя системы (задание 2).
 *
 * Поля:
 * - id       — UUID пользователя (генерируется при регистрации);
 * - login    — уникальное имя для входа;
 * - password — пароль (хранится в виде хэша BCrypt, см. UserServiceImpl/AuthServiceImpl).
 *
 * Модель не содержит JPA-аннотаций: это «чистый» домен.
 * Отображение на таблицу БД выполняет datasource (UserEntity + UserMapper).
 */
public record User(UUID id, String login, String password) {
}
