package datasource.repository;

import datasource.model.GameEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository // Говорит Спрингу, что это компонент для работы с данными (репозиторий)
public interface GameRepository extends CrudRepository<GameEntity, UUID> {
    // Spring сам автоматически создаст методы save(), findById(), delete() и другие под капотом.
}