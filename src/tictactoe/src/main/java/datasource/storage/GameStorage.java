package datasource.storage;

import datasource.model.GameEntity;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameStorage {
    private final Map<UUID, GameEntity> games = new ConcurrentHashMap<>();

    public void save(GameEntity entity) {
        games.put(entity.id(), entity);
    }

    public Optional<GameEntity> getById(UUID id) {
        return Optional.ofNullable(games.get(id));
    }
}