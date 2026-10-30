package domain.model;

import java.util.UUID;

/**
 * Файл: domain/model/GameSession.java
 *
 * Назначение файла: доменная модель «текущей игры» — всё, что сервер знает о партии.
 *
 * Состав (расширен в задании 3):
 * - id         — уникальный номер партии (UUID);
 * - board      — игровое поле;
 * - xPlayerType/xPlayerId — кто играет крестиками (человек или компьютер) и его UUID;
 * - oPlayerType/oPlayerId — кто играет ноликами;
 * - state      — текущее состояние партии (задание 3: ожидание/ход X/ход O/ничья/победа);
 * - winnerId   — UUID победителя (заполняется при WIN_X/WIN_O).
 *
 * Используется как record — неизменяемый снимок состояния.
 * Любое изменение партии в сервисе создаёт новый GameSession и сохраняет его через репозиторий.
 */
public record GameSession(
        UUID id,
        GameBoard board,
        PlayerType xPlayerType,
        UUID xPlayerId,
        PlayerType oPlayerType,
        UUID oPlayerId,
        GameState state,
        UUID winnerId
) {

    /**
     * Convenience-конструктор для партии «только что создана»: пустое поле,
     * один заявленный игрок (крестики), вторая сторона пока неизвестна,
     * состояние — ожидание второго игрока.
     *
     * @param id            идентификатор партии
     * @param firstPlayerType тип первого участника (USER или AI)
     * @param firstPlayerId   UUID первого участника (null для компьютера)
     */
    public GameSession(UUID id, PlayerType firstPlayerType, UUID firstPlayerId) {
        this(id, new GameBoard(), firstPlayerType, firstPlayerId, null, null, GameState.WAITING, null);
    }

    /**
     * Возвращает значок (1 или 2), которым ходит указанный пользователь.
     * Нужен сервису, чтобы проверить: «этот UUID сейчас ходит?».
     *
     * @param playerId проверяемый UUID пользователя
     * @return GameBoard.X, GameBoard.O или -1, если пользователь не участвует в партии
     */
    public int markOf(UUID playerId) {
        if (playerId == null) {
            return -1;
        }
        if (playerId.equals(xPlayerId)) {
            return GameBoard.X;
        }
        if (playerId.equals(oPlayerId)) {
            return GameBoard.O;
        }
        return -1;
    }

    /**
     * UUID стороны, чей сейчас черёд ходить (для computers-стороны возвращается null).
     *
     * @return UUID активного игрока или null, если партия не в активной фазе / ход компьютера
     */
    public UUID currentPlayerId() {
        if (state == GameState.X_TURN) {
            return xPlayerType == PlayerType.USER ? xPlayerId : null;
        }
        if (state == GameState.O_TURN) {
            return oPlayerType == PlayerType.USER ? oPlayerId : null;
        }
        return null;
    }

    /**
     * Является ли партия игрой против компьютера (хотя бы одна сторона — AI).
     *
     * @return true, если второй участник — алгоритм minimax
     */
    public boolean isAgainstComputer() {
        return xPlayerType == PlayerType.AI || oPlayerType == PlayerType.AI;
    }

    /**
     * Имя состояния партии в виде строки — именно так состояние попадает в колонку
     * game_sessions.state таблицы PostgreSQL (см. datasource/mapper/GameMapper).
     *
     * @return name() текущего GameState, например "WAITING" или "X_TURN"
     */
    public String stateName() {
        return state.name();
    }
}
