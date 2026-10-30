package domain.service;

import domain.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Файл: domain/service/UserService.java
 *
 * Назначение файла: контракт сервиса поддержки пользователей (задание 2 —
 * «реализуй поддержку пользователей на всех слоях»).
 *
 *UserService используется:
 * - AuthServiceImpl — при регистрации и проверке логина/пароля;
 * - AuthFilter      — при валидации Basic-заголовка на каждом запросе;
 * - UserController  — endpoint GET /user/{id} (задание 3).
 */
public interface UserService {

    /**
     * Создать пользователя с уникальным логином. Пароль ожидается открытым —
     * сервис сам захэширует его через BCrypt перед сохранением в БД.
     *
     * @param login       логин
     * @param rawPassword открытый пароль из SignUpRequest
     * @return созданный пользователь (сгенерированный UUID)
     * @throws IllegalArgumentException если логин уже занят или данные пусты
     */
    User createUser(String login, String rawPassword);

    /**
     * Найти пользователя по UUID — информация о пользователе для endpoint'а задания 3.
     *
     * @param id UUID пользователя
     * @return Optional с пользователем либо пустой Optional
     */
    Optional<User> findByUuid(UUID id);

    /**
     * Найти пользователя по логину — нужно для авторизации.
     *
     * @param login логин
     * @return Optional с пользователем либо пустой Optional
     */
    Optional<User> findByLogin(String login);

    /**
     * Проверить пару логин/пароль: сравнение открытого пароля с хэшем из БД.
     *
     * @param login    логин из заголовка base64(login:password)
     * @param password пароль из заголовка
     * @return true, если пользователь существует и пароль верный
     */
    boolean validateCredentials(String login, String password);
}
