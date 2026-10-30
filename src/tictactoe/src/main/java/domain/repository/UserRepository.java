package domain.repository;

import domain.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Файл: domain/repository/UserRepository.java
 *
 * Назначение файла: контракт хранения пользователей (задание 2).
 * Реализует его слой datasource поверх Spring Data CrudRepository,
 * использует UserService и AuthFilter (через UserService).
 */
public interface UserRepository {

    /**
     * Сохранить пользователя (создание при регистрации либо обновление пароля).
     *
     * @param user сохраняемый пользователь
     * @return сохранённый пользователь со своим UUID
     */
    User save(User user);

    /**
     * Найти пользователя по уникальному логину — нужно для авторизации
     * (в заголовке Basic приходит login:password) и для проверки занятости логина.
     *
     * @param login логин
     * @return Optional с пользователем либо пустой Optional
     */
    Optional<User> findByLogin(String login);

    /**
     * Найти пользователя по UUID — endpoint GET /user/{id} (задание 3).
     *
     * @param id UUID пользователя
     * @return Optional с пользователем либо пустой Optional
     */
    Optional<User> findById(UUID id);
}
