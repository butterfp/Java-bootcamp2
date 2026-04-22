package datasource.repository.impl;

import datasource.mapper.GameMapper;
import datasource.repository.GameRepository;
import datasource.storage.GameStorage;
import domain.model.GameSession;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class GameRepositoryImpl implements GameRepository {
    private final GameStorage storage;
    private final GameMapper mapper;

    public GameRepositoryImpl(GameStorage storage, GameMapper mapper) {
        this.storage = storage;
        this.mapper = mapper;
    }

    @Override
    public void save(GameSession game) {
        storage.save(mapper.toEntity(game));
    }

    @Override
    public Optional<GameSession> findById(UUID id) {
        return storage.getById(id).map(mapper::toDomain);
    }
}