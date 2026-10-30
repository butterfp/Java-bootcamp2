package domain.repository;

import domain.model.GameSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Файл: domain/repository/GameRepository.java
 *
 * Назначение файла: контракт («что нужно уметь») для хранения текущих игр.
 * Домен зависит только от этого интерфейса и не знает, что игры лежат в PostgreSQL
 * (реализация — datasource/repository/impl/GameRepositoryImpl поверх CrudRepository).
 *
 * Такой приём называется «инверсия зависимостей»: интерфейс живёт в домене,
 * а реализация — в слое данных.
 */
public interface GameRepository {

    /**
     * Сохранить (создать или обновить) партию.
     *
     * @param game сохраняемая игровая сессия
     */
    void save(GameSession game);

    /**
     * Найти партию по идентификатору.
     *
     * @param id UUID игры
     * @return Optional с найденной партией либо пустой Optional, если такой игры нет
     */
    Optional<GameSession> findById(UUID id);

    /**
     * Все партии в состоянии WAITING — список «доступных текущих игр» (задание 3,
     * endpoint GET /game/available).
     *
     * @return список ожидающих второго игрока партий
     */
    List<GameSession> findWaitingGames();

    /**
     * Список всех партий (служебный метод, удобен для отладки).
     *
     * @return все сохранённые игровые сессии
     */
    List<GameSession> findAll();
}
