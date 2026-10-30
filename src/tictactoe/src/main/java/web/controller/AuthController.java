package web.controller;

import domain.model.SignUpRequest;
import domain.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * Файл: web/controller/AuthController.java
 *
 * Назначение файла: контроллер авторизации (задание 2).
 * Содержит ровно два endpoint'а:
 *  - POST /auth/sign-up — регистрация пользователя по модели SignUpRequest (логин + пароль);
 *  - POST /auth/sign-in — авторизация по заголовку Authorization: base64(login:password),
 *                         в ответ возвращает UUID пользователя.
 *
 * Оба адреса объявлены публичными в security/SecurityConfig (разрешены без авторизации),
 * поэтому сюда попадают запросы от неаутентифицированных клиентов.
 *
 * Аннотации:
 *  @RestController — класс отдаёт JSON (каждый метод помечен как @ResponseBody);
 *  @RequestMapping — общий префикс пути для всех методов контроллера.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    /** Сервис авторизации: реализует регистрацию и проверку логина/пароля. */
    private final AuthService authService;

    /**
     * Конструкторное внедрение зависимости (Spring сам передаст бин AuthService).
     *
     * @paramAuthService сервис авторизации
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint регистрации пользователя (задание 2).
     *
     * Тело запроса: {"login": "user", "password": "secret"}.
     * Логика: AuthService.signUp возвращает факт успешной регистрации.
     *
     * @param request модель запросаSignUpRequest (автоматически разбирается из JSON)
     * @return 201 Created, если учётная запись создана;
     *         409 Conflict, если логин уже занят или данные некорректны
     */
    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody SignUpRequest request) {
        boolean registered = authService.signUp(request);
        // Возвращаем только код состояния: тело ответа при регистрации не нужно
        return registered
                ? ResponseEntity.status(HttpStatus.CREATED).build()
                : ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    /**
     * Endpoint авторизации пользователя (задание 2).
     *
     * Заголовок Authorization должен содержать строку base64("login:password")
     * (префикс "Basic " не обязателен — сервис его понимает).
     *
     * @param authorizationHeader значение заголовка Authorization
     * @return 200 OK с телом {"uuid": "..."} при успехе;
     *         401 Unauthorized, если логин/пароль не проверились
     */
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader) {
        try {
            // Метод сервиса возвращает UUID пользователя — именно его требует задание
            UUID userId = authService.signIn(authorizationHeader);
            return ResponseEntity.ok(Map.of("uuid", userId));
        } catch (IllegalArgumentException e) {
            // Побитый/отсутствующий заголовок или неверный пароль -> 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
