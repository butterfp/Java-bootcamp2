package datasource.repository.impl;

import datasource.mapper.UserMapper;
import datasource.model.UserEntity;
import datasource.repository.UserCrudRepository;
import domain.model.User;
import domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Файл: datasource/repository/impl/UserRepositoryImpl.java
 *
 * Назначение файла (задание 1 + задание 2): реализация контракта
 * domain.repository.UserRepository поверх Spring Data CrudRepository<UserEntity, UUID>.
 *
 * Пользователи, как и игры, больше не хранятся в памяти — только таблица app_users.
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserCrudRepository crudRepository;
    private final UserMapper mapper;

    /**
     * @param crudRepository Spring Data репозиторий пользователей
     * @param mapper         конвертер User <-> UserEntity
     */
    public UserRepositoryImpl(UserCrudRepository crudRepository, UserMapper mapper) {
        this.crudRepository = crudRepository;
        this.mapper = mapper;
    }

    /** Сохранение пользователя (INSERT при регистрации, UPDATE при смене пароля). */
    @Override
    public User save(User user) {
        UserEntity saved = crudRepository.save(mapper.toEntity(user));
        return mapper.toDomain(saved); // возвращаем объект с тем id, который записала база
    }

    /** Поиск по логину — запрос выполняет сгенерированный метод findByLogin. */
    @Override
    public Optional<User> findByLogin(String login) {
        return crudRepository.findByLogin(login).map(mapper::toDomain);
    }

    /** Поиск по UUID — endpoint GET /user/{id} (задание 3). */
    @Override
    public Optional<User> findById(UUID id) {
        return crudRepository.findById(id).map(mapper::toDomain);
    }
}
