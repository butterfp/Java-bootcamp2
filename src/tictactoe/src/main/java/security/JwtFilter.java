package security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

// Наследуемся от OncePerRequestFilter-
//В современных версиях Spring Boot
// вместо старых тяжелых классов используют
// удобный наследник OncePerRequestFilter —
// он гарантирует, что фильтр выполнится ровно один раз
// для каждого входящего запроса.
// фильтр сработает ровно 1 раз за один HTTP-запрос
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    // Внедряем наш JwtProvider через конструктор, чтобы проверять токены
    public JwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Ищем токен в заголовках запроса
        String token = getTokenFromRequest(request);

        // 2. Если токен нашелся и он валидный (проверяем через provider)
        if (token != null && jwtProvider.validateAccessToken(token)) {
            // Достаем из токена данные (claims)
            Claims claims = jwtProvider.getClaims(token);

            // Достаем ID пользователя из subject
            UUID userId = UUID.fromString(claims.getSubject());

            // Создаем объект аутентификации для Spring Security (говорим системе, кто пришел)
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userId, null, null); // пока без ролей для простоты

            // Передаем информацию о пользователе в контекст безопасности Spring
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 3. Передаем запрос дальше по цепочке фильтров
        filterChain.doFilter(request, response);
    }

    // Вспомогательный метод: вытаскивает токен из заголовка "Authorization: Bearer <token>"
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization ");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7); // Отрезаем слово "Bearer " и оставляем только сам токен
        }
        return null;
    }
}