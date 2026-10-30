package security;

import domain.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

/**
 * Файл: security/AuthFilter.java
 *
 * Назначение файла (задание 2): фильтр, который проверяет логин и пароль в каждом
 * защищённом HTTP-запросе ДО того, как запрос дойдёт до контроллера.
 *
 * Требования задания, реализованные здесь:
 * - наследование от GenericFilterBean;
 * - реализация метода doFilter;
 * - провалидировать логин и пароль;
 * - если валидация успешна — выполнить запрос (filterChain.doFilter);
 * - если ошибка — добавить в ответ код 401 и НЕ выполнять запрос.
 *
 * Формат учётных данных: заголовок Authorization со значением base64(login:password)
 * (с необязательным префиксом "Basic "). Успешная проверка возвращает UUID пользователя —
 * он кладётся в SecurityContext как principal, и контроллеры достают его оттуда.
 */
public class AuthFilter extends GenericFilterBean {

    /** Сервис авторизации: умеет разбирать base64(login:password) и сверять с БД. */
    private final AuthService authService;

    /**
     * @paramAuthService сервис авторизации (внедряется через SecurityConfig)
     */
    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Основная логика фильтра.
     *
     * Шаги:
     * 1. достаём заголовок Authorization;
     * 2. просим AuthService проверить пару логин/пароль (signIn вернёт UUID);
     * 3. успех -> фиксируем аутентификацию в SecurityContext и идём дальше по цепочке;
     * 4. любая ошибка (нет заголовка, битый base64, неверный пароль) -> 401, запрос не выполняется.
     */
    @Override
    public void doFilter(HttpServletRequest request,
                         HttpServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {

        String authorizationHeader = request.getHeader("Authorization");

        try {
            // Шаг 1+2: валидация логина и пароля
            UUID userId = authService.signIn(authorizationHeader);

            // Шаг 3: помечаем запрос как аутентифицированный.
            // principal = UUID пользователя, authorities пока пустые (роль одна — пользователь)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Только после успешной валидации выполняем сам запрос
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            // Шаг 4: валидация провалена — ставим 401 Unauthorized и НЕ зовём filterChain,
            // то есть запрос к контроллеру вообще не поступает
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
