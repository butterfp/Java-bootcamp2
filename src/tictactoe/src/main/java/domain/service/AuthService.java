package domain.service;

import domain.model.SignUpRequest;

import java.util.UUID;

/**
 * Файл: domain/service/AuthService.java
 *
 * Назначение файла: контракт сервиса авторизации (задание 2).
 * Реализация — domain/service/impl/AuthServiceImpl, вызывается из
 * web/controller/AuthController (endpoint'ы) и security/AuthFilter (валидация заголовка).
 */
public interface AuthService {

    /**
     * Регистрация нового пользователя.
     *
     * @param signUpRequest модель запроса: логин + пароль
     * @return true, если учётная запись создана; false, если логин занят или данные некорректны
     */
    boolean signUp(SignUpRequest signUpRequest);

    /**
     * Авторизация по Basic-заголовку: значение заголовка — base64("login:password").
     *
     * @param authorizationHeader содержимое заголовка Authorization (может быть с префиксом "Basic ")
     * @return UUID пользователя при успешной проверке
     * @throws IllegalArgumentException если заголовок отсутствует, побит или пароль не подошёл
     */
    UUID signIn(String authorizationHeader);
}
