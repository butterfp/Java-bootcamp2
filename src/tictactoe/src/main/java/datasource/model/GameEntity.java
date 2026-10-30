//выполняем Задание 1 (переходим на PostgreSQL и Spring Data JPA), нам нужно:
//
//Превратить этот record в полноценный класс (потому что JPA/Hibernate требует от классов-сущностей наличия стандартного конструктора и возможности менять поля).
//
//Добавить специальные аннотации (метки для Спринга), чтобы он понял, как сохранять этот класс в базу.

package datasource.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity // Говорит Спрингу: "Это сущность, её нужно сохранять в базу данных"
@Table(name = "game_sessions") // Имя таблицы в PostgreSQL
public class GameEntity {

    @Id // Говорит Спрингу: "Это главный уникальный ключ (ID) записи"
    private UUID id;

    private String board; // Обрати внимание: двумерный массив int[][] сложно сохранить в базу напрямую,
    // поэтому обычно его переводят в текст (например, JSON-строку) через маппер.

    // Пустой конструктор обязательно нужен Спрингу
    public GameEntity() {}

    public GameEntity(UUID id, String board) {
        this.id = id;
        this.board = board;
    }

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

    public enum Role() {USER}
}