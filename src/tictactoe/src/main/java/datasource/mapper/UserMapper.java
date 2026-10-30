package datasource.mapper;

import datasource.model.UserEntity;
import domain.model.User;
import org.springframework.stereotype.Component;

/**
 * Файл: datasource/mapper/UserMapper.java
 *
 * Назначение файла (задание 1 + задание 2): конвертер доменной модели User (record)
 * в JPA-сущность UserEntity (таблица app_users) и обратно.
 *
 * Маппер держит границу слоёв: UserService оперирует только domain.model.User,
 * а Hibernate знает только про UserEntity.
 */
@Component
public class UserMapper {

    /**
     * Доменный пользователь -> строка таблицы app_users.
     *
     * @param domain пользователь из бизнес-логики
     * @return сущность для сохранения через CrudRepository
     */
    public UserEntity toEntity(User domain) {
        return new UserEntity(domain.id(), domain.login(), domain.password());
    }

    /**
     * Строка таблицы app_users -> доменный пользователь.
     *
     * @param entity сущность, загруженная из PostgreSQL
     * @return объект User для сервисов и фильтра авторизации
     */
    public User toDomain(UserEntity entity) {
        return new User(entity.getId(), entity.getLogin(), entity.getPassword());
    }
}
