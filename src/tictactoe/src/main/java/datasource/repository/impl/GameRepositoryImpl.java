package datasource.repository.impl;

import datasource.mapper.GameMapper;
import datasource.repository.GameCrudRepository;
import domain.model.GameSession;
import domain.model.GameState;
import domain.repository.GameRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

/**
 * Файл: datasource/repository/impl/GameRepositoryImpl.java
 *
 * Назначение файла (задание 1): реализация контракта domain.repository.GameRepository
 * поверх Spring Data CrudRepository. Именно этот класс замыкает связь «домен -> PostgreSQL».
 *
 * Что изменилось по сравнению с прошлой версией:
 * - удалён класс-хранилище in-memory (GameStorage) — все данные теперь в таблице game_sessions;
 * - вместо хранения объектов в памяти вызывается GameCrudRepository (save/findById/findAll);
 * - конвертацию «домен <-> сущность» выполняет GameMapper.
 */
@Repository
public class GameRepositoryImpl implements GameRepository {

    private final GameCrudRepository crudRepository;
    private final GameMapper mapper;

    /**
     * @param crudRepository сгенерированный Spring Data репозиторий (CrudRepository)
     * @param mapper         конвертер GameSession <-> GameEntity
     */
    public GameRepositoryImpl(GameCrudRepository crudRepository, GameMapper mapper) {
        this.crudRepository = crudRepository;
        this.mapper = mapper;
    }

    /**
     * Сохранение партии: доменный объект превращается в сущность и уходит в PostgreSQL.
     * CrudRepository.save() сам решает, INSERT это (новой id) или UPDATE (id уже есть).
     */
    @Override
    public void save(GameSession game) {
        crudRepository.save(mapper.toEntity(game));
    }

    /**
     * Чтение партии: Hibernate достаёт строку таблицы, маппер собирает доменный объект.
     */
    @Override
    public Optional<GameSession> findById(UUID id) {
        return crudRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * Доступные текущие игры (задание 3): выбираются строки со state = WAITING.
     */
    @Override
    public List<GameSession> findWaitingGames() {
        return crudRepository.findByState(GameState.WAITING.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /** Служебный метод: все партии (отладка, мониторинг). */
    @Override
    public List<GameSession> findAll() {
        // CrudRepository.findAll() возвращает Iterable, а не List — превращаем в стрим стандартным способом
        return StreamSupport.stream(crudRepository.findAll().spliterator(), false)
                .map(mapper::toDomain)
                .toList();
    }
}
