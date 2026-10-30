package domain.service.impl;

import domain.repository.UserRepository;
import domain.model.User;
import domain.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Файл: domain/service/impl/UserServiceImpl.java
 *
 * Назначение файла: реализация сервиса пользователей (задание 2,
 * «поддержка пользователей на всех слоях»).
 *
 * @Service — пометка Spring: класс становится бином и автоматически
 * внедряется туда, где запрашивается интерфейс UserService.
 *
 * Зависимость только от контракта domain.repository.UserRepository —
 * про PostgreSQL, JPA и таблицы этот сервис ничего не знает (инверсия зависимостей).
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Конструкторное внедрение зависимостей (рекомендуемый способ в Spring):
     * Spring сам передаст реализацию UserRepository из слоя datasource
     * и бин BCryptPasswordEncoder из di/GameConfig.
     *
     * @param userRepository  хранилище пользователей
     * @param passwordEncoder кодировщик паролей (хэширование/сверка BCrypt)
     */
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Создание пользователя. UUID генерируется здесь, логин проверяется на уникальность.
     * Открытый пароль перед сохранением превращается в BCrypt-хэш (в базу пароль
     * в открытом виде не попадает никогда).
     */
    @Override
    public User createUser(String login, String rawPassword) {
        if (login == null || login.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Логин и пароль не могут быть пустыми");
        }
        // Уникальность логина: если такой логин уже есть — регистрацию нельзя выполнить
        if (userRepository.findByLogin(login).isPresent()) {
            throw new IllegalArgumentException("Логин " + login + " уже занят");
        }
        return userRepository.save(new User(UUID.randomUUID(), login, passwordEncoder.encode(rawPassword)));
    }

    /** Поиск учётной записи по UUID (endpoint GET /user/{id}). */
    @Override
    public Optional<User> findByUuid(UUID id) {
        return userRepository.findById(id);
    }

    /** Поиск учётной записи по логину (нужен для авторизации). */
    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    /**
     * Проверка пары логин/пароль.
     * Открытый пароль сравнивается с BCrypt-хэшем из базы через PasswordEncoder —
     * простое equals() здесь не подойдёт, так как хэш необратим и всегда «свежий».
     *
     * @param login    логин из заголовка base64(login:password)
     * @param password открытый пароль из заголовка
     * @return true, если пользователь существует и пароль верный
     */
    @Override
    public boolean validateCredentials(String login, String password) {
        Optional<User> user = userRepository.findByLogin(login);
        // 1. пользователя нет -> false; 2. пароль пустой -> false;
        // 3. BCrypt сверяет открытый пароль с сохранённым хэшем
        return user.isPresent()
                && password != null
                && passwordEncoder.matches(password, user.get().password());
    }
}
