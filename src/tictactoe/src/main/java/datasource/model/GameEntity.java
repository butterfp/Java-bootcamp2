package datasource.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Файл: datasource/model/GameEntity.java
 *
 * Назначение файла (задание 1): JPA-сущность, описывающая таблицу текущих партий
 * в PostgreSQL. Специальные аннотации, которые просит добавить задание:
 * - @Entity  — говорит Hibernate: «объекты этого класса нужно сохранять в базу данных»;
 * - @Table   — имя таблицы в PostgreSQL (game_sessions);
 * - @Id      — первичный ключ записи (UUID партии);
 * - @Column  — настройки конкретных колонок (тип, обязательность).
 *
 * Почему не record: JPA/Hibernate требует от сущности публичный конструктор без аргументов
 * и возможности заполнять поля через сеттеры — поэтому здесь обычный класс.
 *
 * Хранение доски: двумерный массив int[3][3] в реляционную колонку напрямую не ложится,
 * поэтому доска сериализуется в JSON-строку (см. datasource/mapper/GameMapper),
 * а в БД хранится как TEXT.
 */
@Entity
@Table(name = "game_sessions")
public class GameEntity {

    /** Первичный ключ партии (UUID). */
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** Игровое поле в виде JSON-строки, например [[1,0,2],[0,1,0],[0,0,2]]. */
    @Column(name = "board", nullable = false, columnDefinition = "TEXT")
    private String board;

    /** Кто играет крестиками: USER / AI (задание 3). */
    @Column(name = "x_player_type")
    private String xPlayerType;

    /** UUID игрока крестиков (null, если сторону занимает компьютер). */
    @Column(name = "x_player_id")
    private UUID xPlayerId;

    /** Кто играет ноликами: USER / AI. */
    @Column(name = "o_player_type")
    private String oPlayerType;

    /** UUID игрока ноликов (null для компьютера или пока соперник не подключился). */
    @Column(name = "o_player_id")
    private UUID oPlayerId;

    /** Состояние партии: WAITING / X_TURN / O_TURN / DRAW / WIN_X / WIN_O (задание 3). */
    @Column(name = "state", nullable = false)
    private String state;

    /** UUID победителя (заполняется только при WIN_X / WIN_O). */
    @Column(name = "winner_id")
    private UUID winnerId;

    /**
     * Пустой конструктор обязателен для Hibernate: он создаёт экземпляры сущности
     * при загрузке строк из таблицы и затем заполняет поля.
     */
    public GameEntity() {
    }

    /**
     * Конструктор со всеми полями — используется маппером при сохранении партии.
     */
    public GameEntity(UUID id, String board, String xPlayerType, UUID xPlayerId,
                      String oPlayerType, UUID oPlayerId, String state, UUID winnerId) {
        this.id = id;
        this.board = board;
        this.xPlayerType = xPlayerType;
        this.xPlayerId = xPlayerId;
        this.oPlayerType = oPlayerType;
        this.oPlayerId = oPlayerId;
        this.state = state;
        this.winnerId = winnerId;
    }

    // --- Геттеры и сеттеры: нужны Hibernate для чтения/записи полей ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getXPlayerType() {
        return xPlayerType;
    }

    public void setXPlayerType(String xPlayerType) {
        this.xPlayerType = xPlayerType;
    }

    public UUID getXPlayerId() {
        return xPlayerId;
    }

    public void setXPlayerId(UUID xPlayerId) {
        this.xPlayerId = xPlayerId;
    }

    public String getOPlayerType() {
        return oPlayerType;
    }

    public void setOPlayerType(String oPlayerType) {
        this.oPlayerType = oPlayerType;
    }

    public UUID getOPlayerId() {
        return oPlayerId;
    }

    public void setOPlayerId(UUID oPlayerId) {
        this.oPlayerId = oPlayerId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public UUID getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(UUID winnerId) {
        this.winnerId = winnerId;
    }
}
