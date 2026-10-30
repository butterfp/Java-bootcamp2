package dto;

// dto для ответа клиенту с парой токенов
public class AuthResponse {
    private String accessToken;
    //(доступ) короткоживущий токен (у нас он живет 5 минут).
// Клиент прикрепляет его к каждому запросу в заголовке Authorization: Bearer <token>,
// чтобы доказать серверов, кто он такой.
    private String refreshToken; //(обновление) долгоживущий токен (у нас на 30 дней).
// Он нужен только для одной цели: когда у клиента истекает Access Token (через 5 минут),
// клиент отправляет Refresh Token на специальный эндпоинт и получает новую пару токенов,
// не заставляя пользователя снова вводить логин и пароль.

    // Конструктор
    public AuthResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // Геттеры (нужны Spring, чтобы превратить объект в JSON)
    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}