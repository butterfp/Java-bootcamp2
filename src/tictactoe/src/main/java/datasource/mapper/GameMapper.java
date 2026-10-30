package datasource.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import datasource.model.GameEntity;
import domain.model.GameBoard;
import domain.model.GameSession;
import domain.model.GameState;
import domain.model.PlayerType;
import org.springframework.stereotype.Component;

/**
 * Файл: datasource/mapper/GameMapper.java
 *
 * Назначение файла (задание 1): конвертер между доменной моделью GameSession
 * (с ней работает бизнес-логика) и JPA-сущностью GameEntity (так она лежит в PostgreSQL).
 *
 * Зачем нужен маппер:
 * - домен не должен знать про Hibernate и JSON-представление доски;
 * - сущность не должна просачиваться в сервисы и контроллеры;
 * - двумерный массив int[3][3] в реляционной колонке хранить нельзя,
 *   поэтому ObjectMapper сериализует его в JSON-строку и обратно.
 *
 * @Component — Spring создаст один экземпляр маппера и внедрит его в репозиторий.
 */
@Component
public class GameMapper {

    /** Инструмент Jackson для перевода объектов/массивов в JSON и обратно. */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Доменная партия -> строка таблицы game_sessions.
     * Доска превращается в JSON-строку, enum'ы — в свои имена (WAITING, USER...).
     *
     * @param domain игровая сессия из бизнес-логики
     * @return сущность, готовая к сохранению через CrudRepository
     */
    public GameEntity toEntity(GameSession domain) {
        try {
            String boardJson = objectMapper.writeValueAsString(domain.board().board());
            return new GameEntity(
                    domain.id(),
                    boardJson,
                    domain.xPlayerType() == null ? null : domain.xPlayerType().name(),
                    domain.xPlayerId(),
                    domain.oPlayerType() == null ? null : domain.oPlayerType().name(),
                    domain.oPlayerId(),
                    domain.stateName(),
                    domain.winnerId());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Ошибка при конвертации доски в JSON", e);
        }
    }

    /**
     * Строка таблицы game_sessions -> доменная партия.
     * JSON-строка снова становится int[][], строковые коды состояний и типов игроков
     * превращаются обратно в enum'а GameState / PlayerType.
     *
     * @param entity сущность, загруженная Hibernate из PostgreSQL
     * @return объект GameSession для бизнес-логики
     */
    public GameSession toDomain(GameEntity entity) {
        try {
            int[][] boardArray = objectMapper.readValue(entity.getBoard(), int[][].class);
            return new GameSession(
                    entity.getId(),
                    new GameBoard(boardArray),
                    entity.getXPlayerType() == null ? null : PlayerType.valueOf(entity.getXPlayerType()),
                    entity.getXPlayerId(),
                    entity.getOPlayerType() == null ? null : PlayerType.valueOf(entity.getOPlayerType()),
                    entity.getOPlayerId(),
                    GameState.valueOf(entity.getState()),
                    entity.getWinnerId());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Ошибка при чтении доски из JSON", e);
        }
    }
}
