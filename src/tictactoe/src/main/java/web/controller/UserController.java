package web.controller;

import domain.model.User;
import domain.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.mapper.WebMapper;
import web.model.UserInfoDTO;

import java.util.UUID;

/**
 * Файл: web/controller/UserController.java
 *
 * Назначение файла: endpoint получения информации о пользователе по UUID
 * (последний пункт задания 3).
 *
 * Адрес: GET /user/{id} — защищённый, требует корректный заголовок
 * Authorization: base64(login:password) (см. security/AuthFilter и SecurityConfig).
 *
 * Ответ содержит только публичные поля (UUID и логин): пароль никогда не
 * покидает слой БД — это делает WebMapper.toUserResponse.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /** Сервис поддержки пользователей (задание 2) — поиск по UUID. */
    private final UserService userService;

    /** Конвертер доменного User -> UserInfoDTO. */
    private final WebMapper webMapper;

    /**
     * Конструкторное внедрение зависимостей.
     *
     * @param userService сервис пользователей
     * @param webMapper   маппер ответов
     */
    public UserController(UserService userService, WebMapper webMapper) {
        this.userService = userService;
        this.webMapper = webMapper;
    }

    /**
     * Получить информацию о пользователе по его UUID.
     *
     * @param id UUID пользователя из пути запроса (/user/3f2a...-...)
     * @return 200 OK с телом {"id": "...", "login": "..."} либо 404 Not Found,
     *         если пользователь с таким идентификатором отсутствует в базе
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserInfoDTO> getUser(@PathVariable("id") UUID id) {
        // Optional позволяет не бросать исключение вручную: empty -> 404
        return userService.findByUuid(id)
                .map(webMapper::toUserResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Справочный метод: возвращает доменную модель пользователя или 404.
     * Используется другими контроллерами/сервисами при проверке участия в игре.
     *
     * @param id UUID пользователя
     * @return найденный пользователь
     * @throws IllegalArgumentException если пользователь не найден (-> 400/404 в обработчике)
     */
    public User requireUser(UUID id) {
        return userService.findByUuid(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + id));
    }
}
