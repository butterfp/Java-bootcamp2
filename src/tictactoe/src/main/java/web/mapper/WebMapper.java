package web.mapper;

import domain.model.GameSession;
import domain.model.PlayerType;
import domain.model.User;
import org.springframework.stereotype.Component;
import web.model.GameResponseDTO;
import web.model.PlayerInfoDTO;
import web.model.UserInfoDTO;

/**
 * Файл: web/mapper/WebMapper.java
 *
 * Назначение файла: конвертер доменных моделей в DTO веб-слоя (JSON для клиента).
 *
 * @Component — один экземпляр маппера внедряется во все контроллеры.
 * Маппер нужен, чтобы наружу не уходили лишние детали (например, пароль пользователя)
 * и чтобы клиент видел стабильный контракт независимо от изменений внутри домена.
 */
@Component
public class WebMapper {

    /**
     * Игровая сессия -> ответ о партии (задание 3: участники, значки, состояние, победитель).
     *
     * @param game   доменная партия
     * @param status человекочитаемый статус из TicTacToeService.getGameStatus
     * @return DTO для сериализации в JSON
     */
    public GameResponseDTO toResponse(GameSession game, String status) {
        return new GameResponseDTO(
                game.id(),
                toPlayer(game.xPlayerType(), game.xPlayerId()),
                toPlayer(game.oPlayerType(), game.oPlayerId()),
                game.board(),
                game.stateName(),
                game.currentPlayerId(),
                game.winnerId(),
                status);
    }

    /**
     * Пользователь -> публичная информация о нём (GET /user/{id}).
     * Пароль не переносится: это секретная часть модели.
     *
     * @param user доменный пользователь
     * @return DTO с UUID и логином
     */
    public UserInfoDTO toUserResponse(User user) {
        return new UserInfoDTO(user.id(), user.login());
    }

    /**
     * Служебный метод: сторона партии -> PlayerInfoDTO.
     * Если сторона ещё не занята (тип null), возвращаем null — так клиент понимает,
     * что игра ожидает второго игрока.
     */
    private PlayerInfoDTO toPlayer(PlayerType type, UUID playerId) {
        if (type == null) {
            return null;
        }
        return new PlayerInfoDTO(type.name(), playerId);
    }
}
