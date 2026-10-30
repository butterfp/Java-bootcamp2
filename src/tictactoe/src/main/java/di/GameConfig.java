package di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Файл: di/GameConfig.java
 *
 * Назначение файла: дополнительная Spring-конфигурация приложения (слой di).
 * Здесь описаны бины, которые нельзя пометить аннотацией @Component/@Service
 * внутри самого класса — они создаются вручную, потому что принадлежат сторонним
 * библиотекам или требуют выбора конкретной реализации.
 *
 * ВАЖНО: старый in-memory класс-хранилище GameStorage из этого файла удалён
 * (задание 1) — все данные теперь живут только в PostgreSQL через JPA-репозитории
 * (datasource/repository/*CrudRepository, наследники CrudRepository).
 */
@Configuration // класс содержит методы-описания бинов (@Bean)
public class GameConfig {

    /**
     * Бин кодировщика паролей (задание 2).
     *
     * Используется:
     *  - UserServiceImpl.createUser — хэширует пароль перед сохранением в таблицу app_users;
     *  - UserServiceImpl.validateCredentials — сверяет открытый пароль из заголовка
     *    base64(login:password) с хэшем из базы.
     *
     * BCrypt выбран потому, что он:
     *  - необратим (по хэшу нельзя восстановить пароль);
     *  - содержит «соль» внутри результата, поэтому одинаковые пароли дают разные хэши;
     *  - намеренно медленный, что мешает перебору.
     *
     * @return реализация PasswordEncoder для Spring Security
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
