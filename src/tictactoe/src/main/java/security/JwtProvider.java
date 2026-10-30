//JWT Provider(обеспечивающий / предоставляющий) — это класс,
// который создаёт JWT-токены,проверяет их подпись и срок,
// извлекает из них данные (username, роли)

package security;

// 1. Нужен для работы с «начинкой» токена (claims — это утверждения или данные, спрятанные внутри токена)
import io.jsonwebtoken.Claims;

// 2. Главный строитель токенов от библиотеки JJWT. Именно он собирает token через Jwts.builder()
import io.jsonwebtoken.Jwts;

// 3. Понадобится позже, чтобы расшифровывать секретный ключ из закодированного вида (например, Base64)
import io.jsonwebtoken.io.Decoders;

// 4. Помощник для генерации и управления криптографическими ключами подписи
import io.jsonwebtoken.security.Keys;

// 5. Наш собственный энум с ролями пользователей (USER), который мы положим внутрь токена
import domain.model.Role;

// 6. Специальный тип данных из Java для криптографических секретных ключей
import javax.crypto.SecretKey;

// 7. Общий родительский интерфейс для всех ключей шифрования (технически сейчас можно даже убрать, так как мы используем SecretKey, но пусть будет)
import java.security.Key;

// 8. Точная точка во времени в формате UTC (нужна для перевода даты и времени в понятный для токена формат)
import java.time.Instant;

// 9. Привычный нам календарный объект «текущая дата и время» (например, чтобы прибавить к нему 5 минут)
import java.time.LocalDateTime;

// 10. Помощник, который переводит «просто время» в привязку к твоему текущему часовому поясу
import java.time.ZoneId;

// 11. Класс из старого доброго Java API, который библиотека JJWT требует для установки срока годности токена (.setExpiration)
import java.util.Date;

// 12. Стандартный список Java (List), чтобы хранить несколько ролей пользователя
import java.util.List;

// 13. Универсальный уникальный идентификатор (UUID), который мы используем как ID пользователя
import java.util.UUID;

public class JwtProvider {

    // Секретный ключ для подписи Access токена
    private final SecretKey jwtSecretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
//final-сгенерировали ПРИ ЗАПУСКЕ, и больше объект ключа изменить нельзя.
//SecretKey-тип данных
//Keys.secretKeyFor-инструмент из библиотеки JJWT (Keys),который умеет
// автоматически генерировать случайные криптографически стойкие ключи
//io.jsonwebtoken.SignatureAlgorithm.HS256
//Это алгоритм шифрования, HS256 (HMAC используя SHA-256) — это стандартный, надежный
// и быстрый алгоритм для подписи JWT-токенов симметричным ключом
// (когда и подписывает, и проверяет один и тот же сервер).
    public String generateAccessToken(UUID id, List<Role> roles) {
        // 1. Устанавливаем время жизни токена (например, 5 минут от текущего момента)
        LocalDateTime now = LocalDateTime.now();
        Instant accessExpirationInstant = now.plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant();
        Date accessExpiration = Date.from(accessExpirationInstant);

        // 2. Строим сам токен через Jwts.builder()
        return Jwts.builder()
                .setSubject(id.toString())                      // Записываем ID пользователя
                .claim("roles", roles)                          // Записываем список ролей
                .setExpiration(accessExpiration)                // Устанавливаем срок годности
                .signWith(jwtAccessSecret)                      // Подписываем секретным ключом
                .compact();                                     // Сжимаем в готовую строку токена
    }

    public String generateRefreshToken(UUID id) {
        // 1. Время жизни рефреш-токена ставим дольше (например, 30 дней)
        LocalDateTime now = LocalDateTime.now();
        Instant refreshExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        Date refreshExpiration = Date.from(refreshExpirationInstant);

        // 2. Строим рефреш-токен (сюда роли НЕ пишем, только ID пользователя)
        return Jwts.builder()
                .setSubject(id.toString())                  // Записываем ID как субъект
                .setExpiration(refreshExpiration)           // Срок годности на 30 дней
                .signWith(jwtSecretKey)                     // Подписываем тем же секретным ключом
                .compact();                                 // Сжимаем в строку
    }
    //ТО ЧТО БУДЕТ НИЖЕ-валидация токенов (validateAccessToken и validateRefreshToken)
    // извлечение данных (getClaims).
    // 1. Метод для извлечения "начинки" (claims) из токена
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)               // Передаем наш секретный ключ для проверки подписи
                .build()                                   // Собираем парсер
                .parseClaimsJws(token)                     // Расшифровываем токен (выбросит ошибку, если подпись не совпала или просрочена)
                .getBody();                                // Достаем сами данные (Claims)
    }
    // 2. Валидация Access токена
    public boolean validateAccessToken(String accessToken) {
        try {
            getClaims(accessToken);                       // Пробуем прочитать токен
            return true;                                  // Если ошибок не было — токен валидный
        } catch (Exception e) {
            return false;                                 // Если токен подделан/просрочен — возвращаем false
        }
    }
    // 3. Валидация Refresh токена
    public boolean validateRefreshToken(String refreshToken) {
        try {
            getClaims(refreshToken);                      // Пробуем прочитать токен
            return true;                                  // Если ошибок не было — токен валидный
        } catch (Exception e) {
            return false;                                 // Если токен подделан/просрочен — возвращаем false
        }
    }
}