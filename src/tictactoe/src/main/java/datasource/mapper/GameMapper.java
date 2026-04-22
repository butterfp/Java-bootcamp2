package datasource.mapper;

import datasource.model.GameEntity;
import domain.model.GameBoard;
import domain.model.GameSession;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {

    public GameEntity toEntity(GameSession domain) {
        return new GameEntity(domain.id(), domain.board().board());
    }

    public GameSession toDomain(GameEntity entity) {
        return new GameSession(entity.id(), new GameBoard(entity.board()));
    }
}