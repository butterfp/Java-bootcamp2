package domain.model;

/**
 * Файл: domain/model/SignUpRequest.java
 *
 * Назначение файла: модель запроса на регистрацию (задание 2).
 * Приходит в POST /auth/sign-up в теле JSON вида {"login": "...", "password": "..."}.
 *
 * UUID здесь нет — его генерирует сервер при успешной регистрации.
 * Пароль в этой модели ещё не захэширован: хэширование выполняет AuthService/UserService.
 */
public record SignUpRequest(String login, String password) {

    /**
     * Компактный конструктор record: первичная валидация формы регистрации.
     * Пустой логин или пароль дальше приведут к ошибке 400 (см. web/exception).
     */
    public SignUpRequest {
        if (login != null) {
            login = login.trim();
        }
    }
}
