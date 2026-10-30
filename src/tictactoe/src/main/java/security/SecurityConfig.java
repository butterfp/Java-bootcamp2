package security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // Аннотация говорит Spring, что это класс с настройками
@EnableWebSecurity // Включает модули веб-безопасности Spring Security
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    // Внедряем JwtProvider через конструктор, чтобы передать его в наш фильтр
    public SecurityConfig(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Bean // Регистрируем настройки цепочки фильтров в виде бина Spring
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Отключаем защиту от CSRF
                // (для REST API с JWT оно не нужно)
                //Cross-Site Request Forgery (межсайтовая подделка запроса)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        // Отключаем сессии:
                        // сервер ничего не запоминает, всё через токены
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        // Разрешаем всем заходить на эндпоинты аутентификации (логин/рефреш)
                        .anyRequest().authenticated()
                        // Все остальные запросы требуют наличия валидного токена
                )
                // Вставляем наш JwtFilter перед стандартным фильтром проверки пароля от Spring
                .addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}