package datasource.repository;

import datasource.model.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Файл: datasource/repository/UserCrudRepository.java
 *
 * Назначение файла (задание 1 + задание 2): Spring Data репозиторий пользователей.
 * Родитель — CrudRepository<UserEntity, UUID>, как требует задание 1
 * («используй у репозиториев в качестве родителя CrudRepository»).
 */
@Repository
public interface UserCrudRepository extends CrudRepository<UserEntity, UUID> {

    /**
     * Поиск по логину (derived query: SELECT * FROM app_users WHERE login = ?).
     * Нужен при авторизации: в Basic-заголовке приходит именно логин, а не UUID.
     *
     * @param login логин пользователя
     * @return Optional с найденной сущностью
     */
    Optional<UserEntity> findByLogin(String login);
}
