/**
 * AuthController — это контроллер, который отвечает за аутентификацию пользователей.
 *
 * Назначение:
 * 1. Принимает запросы на вход в систему (логин и пароль) и выдает JWT-токены (Access и Refresh).
 * 2. Эти эндпоинты находятся в открытом доступе (без проверки токена в SecurityConfig),
 *    так как пользователь еще не авторизован и только пытается получить свои «ключи доступа».
 * 3. Как и любой контроллер, он принимает HTTP-запрос, вызывает сервис аутентификации (AuthService)
 *    и возвращает клиенту ответ (например, в виде AuthResponse).
 */
package controller;

import dto.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.AuthService;


//Контроллер — это класс, который принимает HTTP-запросы, разбирает их (URL, параметры, тело),
// вызывает сервис и возвращает ответ.
@RestController// является точкой входа в приложение:
// клиент шлёт запрос → контроллер его обрабатывает → вызывает сервис → отдаёт ответ
//REST (Representational State Transfer) — это набор правил/принципов,
// как организовать общение между клиентом и сервером.
//Проще: REST — это «как правильно делать API», чтобы оно было понятным, предсказуемым и удобным.
@RequestMapping("/auth") // Базовый путь для всех эндпоинтов в этом классе (начинаются с /auth)
public class AuthController {

    private final AuthService authService;

    // Внедряем AuthService через конструктор
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Эндпоинт для входа: POST /auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestParam String username, @RequestParam String password) {
        // Вызываем сервис, который выдает токены
        AuthResponse response = authService.login(username, password);

        // Возвращаем HTTP-ответ со статусом 200 OK и нашими токенами в теле
        return ResponseEntity.ok(response);
    }
}
// Эндпоинт для обновления токенов: POST /auth/refresh
@PostMapping("/refresh")//POST — это один из HTTP-методов (наряду с GET, PUT, DELETE и др.).
// используется, когда клиент отправляет данные на сервер,
// чтобы создать ресурс или выполнить действие, меняющее состояние.
public ResponseEntity<AuthResponse> refresh(@RequestParam String refreshToken) {
    // Вызываем сервис обновления и возвращаем новую пару токенов
    AuthResponse response = authService.refresh(refreshToken);
    return ResponseEntity.ok(response);
}