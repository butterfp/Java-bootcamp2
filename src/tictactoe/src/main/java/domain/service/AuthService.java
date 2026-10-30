package service;

import domain.model.Role;
import org.springframework.stereotype.Service;
import security.JwtProvider;

import java.util.List;
import java.util.UUID;

@Service // Говорим Spring, что это бизнес-сервис, которым можно управлять
public class AuthService {

    private final JwtProvider jwtProvider;

    // Внедряем JwtProvider, чтобы уметь выпускать токены после успешного логина
    public AuthService(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    // Метод имитации входа пользователя (позже здесь будет проверка пароля из базы)
    public AuthResponse login(String username, String password) {
        // 1. Здесь в будущем мы будем искать пользователя в БД и сверять пароль
        // Для примера пока создадим фейковый UUID и роль
        UUID fakeUserId = UUID.randomUUID();
        List<Role> fakeRoles = List.of(Role.USER);

        // 2. Генерируем пару токенов через JwtProvider
        String accessToken = jwtProvider.generateAccessToken(fakeUserId, fakeRoles);
        String refreshToken = jwtProvider.generateRefreshToken(fakeUserId);

        // 3. Возвращаем токены клиенту (упаковываем в специальный DTO-ответ)
        return new AuthResponse(accessToken, refreshToken);
    }
}