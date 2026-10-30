package datasource.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Файл: datasource/model/UserEntity.java
 *
 * Назначение файла (задание 1 + задание 2): JPA-сущность таблицы пользователей app_users.
 * Аннотации те же, что требуются заданием 1: @Entity (сохранять в БД),
 * @Table (имя таблицы), @Id (первичный ключ), @Column (настройки колонок).
 *
 * Поля соответствуют модели пользователя из задания 2: UUID, логин, пароль.
 * В колонке password лежит BCrypt-хэш, открытый пароль в базу не записывается.
 */
@Entity
@Table(name = "app_users")
public class UserEntity {

    /** Уникальный идентификатор пользователя (задание 2: «у пользователей будет UUID»). */
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** Логин: уникальное имя для входа. unique=true — ограничение на уровне схемы БД. */
    @Column(name = "login", nullable = false, unique = true)
    private String login;

    /** Пароль (BCrypt-хэш). */
    @Column(name = "password", nullable = false)
    private String password;

    /** Пустой конструктор обязателен для Hibernate. */
    public UserEntity() {
    }

    /** Конструктор со всеми полями — используется UserMapper при сохранении. */
    public UserEntity(UUID id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
