package datasource.repository;

import datasource.model.GameEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Файл: datasource/repository/GameCrudRepository.java
 *
 * Назначение файла (задание 1): Spring Data репозиторий игр.
 * Родитель — CrudRepository<GameEntity, UUID>: Spring сам генерирует реализацию
 * методов save(), findById(), findAll(), deleteById() и т.д., обращаться к JDBC вручную не нужно.
 *
 * Параметры дженерика:
 * - GameEntity — тип сущности (класс, помеченный @Entity);
 * - UUID       — тип первичного ключа.
 *
 * Дополнительные методы описаны по правилам «derived queries»: Spring читает их имена
 * и строит SQL-запросы автоматически (WHERE state = ?).
 */
@Repository
public interface GameCrudRepository extends CrudRepository<GameEntity, UUID> {

    /**
     * Все партии в указанном состоянии — используется для endpoint'а
     * «получить доступные текущие игры» (state = WAITING).
     *
     * @param state имя состояния GameState (например "WAITING")
     * @return список подходящих сущностей
     */
    List<GameEntity> findByState(String state);
}
