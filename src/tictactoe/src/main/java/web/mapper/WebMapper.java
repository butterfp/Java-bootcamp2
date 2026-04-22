package web.mapper;

import domain.model.GameSession;
import web.model.GameResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class WebMapper {
    public GameResponseDTO toResponse(GameSession domain, String status) {
        return new GameResponseDTO(domain.id(), domain.board(), status);
    }
}