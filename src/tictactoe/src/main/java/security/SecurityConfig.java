package security;

import domain.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Файл: security/SecurityConfig.java
 *
 * Назначение файла (задание 2): Spring Configuration с настройками безопасности.
 *
 * Что требует задание и что здесь сделано:
 * - описан Bean, возвращающий SecurityFilterChain;
 * - доступ без авторизации разрешён к endpoint'ам регистрации и авторизации (/auth/**);
 * - для всех остальных endpoint'ов требуется авторизация (anyRequest().authenticated());
 * - AuthFilter используется в качестве фильтра (вставлен перед стандартным
 *   UsernamePasswordAuthenticationFilter).
 */
@Configuration          // класс содержит описания бинов Spring
@EnableWebSecurity      // включает модуль веб-безопасности Spring Security
public class SecurityConfig {

    private final AuthService authService;

    /**
     * Конструкторное внедрение сервиса авторизации — он нужен фильтру для проверки
     * логина/пароля по базе PostgreSQL.
     *
     * @param authService сервис авторизации (AuthServiceImpl)
     */
    public SecurityConfig(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Цепочка фильтров безопасности для всего приложения.
     *
     * Настройки по порядку:
     * 1. csrf().disable() — для REST API без сессий CSRF-токены не используются;
     * 2. STATELESS — сервер не хранит сессии, состояние каждого запроса определяется
     *    заголовком Authorization;
     * 3. правила доступа: /auth/sign-up и /auth/sign-in открыты всем, остальное — только
     *    авторизованным;
     * 4. AuthFilter добавляется в цепочку: он проверяет base64(login:password) и либо
     *    пропускает запрос, либо отвечает 401.
     *
     * @param http конфигура builder HTTP-безопасности от Spring
     * @return готовая SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Публичные endpoint'ы: регистрация и авторизация
                        .requestMatchers("/auth/sign-up", "/auth/sign-in").permitAll()
                        // Любой другой запрос должен быть выполнен авторизованным пользователем
                        .anyRequest().authenticated())
                // Наш фильтр проверяет логин/пароль раньше стандартного фильтра Spring Security
                .addFilterBefore(new AuthFilter(authService), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
