package datasource.repository;

import domain.model.GameSession;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository {
    void save(GameSession game);
    Optional<GameSession> findById(UUID id);
}