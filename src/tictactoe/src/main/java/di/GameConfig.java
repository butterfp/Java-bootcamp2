package di;

import datasource.mapper.GameMapper;
import datasource.repository.GameRepository;
import datasource.repository.impl.GameRepositoryImpl;
import datasource.storage.GameStorage;
import domain.service.TicTacToeService;
import domain.service.impl.TicTacToeServiceImpl;
import web.controller.GameController;
import web.mapper.WebMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameConfig {

    @Bean
    public GameStorage gameStorage() {
        return new GameStorage();
    }

    @Bean
    public GameMapper gameMapper() {
        return new GameMapper();
    }

    @Bean
    public GameRepository gameRepository(GameStorage storage, GameMapper mapper) {
        return new GameRepositoryImpl(storage, mapper);
    }

    @Bean
    public TicTacToeService ticTacToeService(GameRepository repository) {
        return new TicTacToeServiceImpl(repository);
    }

    @Bean
    public WebMapper webMapper() {
        return new WebMapper();
    }

    @Bean
    public GameController gameController(TicTacToeService service, WebMapper mapper) {
        return new GameController(service, mapper);
    }
}