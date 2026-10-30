package domain.service.impl;

import domain.model.SignUpRequest;
import domain.model.User;
import domain.service.AuthService;
import domain.service.UserService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

/**
 * Файл: domain/service/impl/AuthServiceImpl.java
 *
 * Назначение файла: реализация сервиса авторизации (задание 2).
 *
 * Требования задания, закрытые здесь:
 * - метод регистрации принимает SignUpRequest и возвращает факт успешной регистрации (boolean);
 * - метод авторизации принимает в заголовке логин и пароль в виде base64(login:password)
 *   и возвращает UUID пользователя;
 * - сервис использует UserService (не обращается к БД напрямую).
 *
 * @Service — бином Spring; внедряется в AuthController и AuthFilter.
 */
@Service
public class AuthServiceImpl implements AuthService {

    /** Префикс схемы Basic в HTTP-заголовке Authorization. */
    private static final String BASIC_PREFIX = "Basic ";

    private final UserService userService;

    /**
     * Конструкторное внедрение: вся работа с пользователями делегируется UserService.
     *
     * @param userService сервис поддержки пользователей
     */
    public AuthServiceImpl(UserService userService) {
        this.userService = userService;
    }

    /**
     * Регистрация пользователя.
     * Валидирует форму, делегирует создание UserService и превращает исключение
     * «логин занят» в спокойный ответ false (как требует задание: «возвращает факт
     * успешной регистрации»).
     *
     * @param signUpRequest логин + пароль
     * @return true — учётная запись создана; false — данные некорректны или логин занят
     */
    @Override
    public boolean signUp(SignUpRequest signUpRequest) {
        if (signUpRequest == null
                || signUpRequest.login() == null || signUpRequest.login().isBlank()
                || signUpRequest.password() == null || signUpRequest.password().isBlank()) {
            return false; // пустая форма регистрации
        }
        try {
            userService.createUser(signUpRequest.login(), signUpRequest.password());
            return true;
        } catch (IllegalArgumentException e) {
            // Логин уже занят либо данные не прошли проверку — регистрация не удалась
            return false;
        }
    }

    /**
     * Авторизация по заголовку Authorization: Basic base64("login:password").
     *
     * Алгоритм:
     * 1. отрезать префикс "Basic " (он может отсутствовать — допускаем оба варианта);
     * 2. раскодировать base64 в строку "login:password";
     * 3. разбить по первому двоеточию (пароль может содержать «:»);
     * 4. сверить пару через UserService.validateCredentials;
     * 5. вернуть UUID найденного пользователя.
     *
     * @param authorizationHeader значение заголовка Authorization
     * @return UUID успешно вошедшего пользователя
     * @throws IllegalArgumentException при побитом заголовке или неверных данных
     */
    @Override
    public UUID signIn(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new IllegalArgumentException("Заголовок Authorization отсутствует");
        }

        // Шаг 1: снимаем префикс схемы, если он есть
        String encoded = authorizationHeader.startsWith(BASIC_PREFIX)
                ? authorizationHeader.substring(BASIC_PREFIX.length()).trim()
                : authorizationHeader.trim();

        // Шаг 2: base64 -> "login:password"
        String decoded;
        try {
            decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Не удалось раскодировать base64 из заголовка");
        }

        // Шаг 3: делим ровно по первому двоеточию
        int separatorIndex = decoded.indexOf(':');
        if (separatorIndex <= 0 || separatorIndex == decoded.length() - 1) {
            throw new IllegalArgumentException("Ожидался формат base64(login:password)");
        }
        String login = decoded.substring(0, separatorIndex);
        String password = decoded.substring(separatorIndex + 1);

        // Шаг 4+5: проверяем пару и достаём UUID пользователя
        Optional<User> user = userService.findByLogin(login);
        if (user.isEmpty() || !userService.validateCredentials(login, password)) {
            throw new IllegalArgumentException("Неверный логин или пароль");
        }
        return user.get().id();
    }
}
