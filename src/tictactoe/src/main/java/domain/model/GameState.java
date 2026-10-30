package domain.model;

/**
 * Файл: domain/model/GameState.java
 *
 * Назначение файла: единое описание всех состояний текущей партии (задание 3).
 * Раньше «идёт игра / победа / ничья» определялись строкой и повторным сканированием
 * доски; теперь состояние хранится явно в GameSession и обновляется сервисом.
 *
 * Состояния покрывают весь жизненный цикл партии:
 * WAITING -> X_TURN/O_TURN -> DRAW | WIN_X | WIN_O.
 */
public enum GameState {

    /** Ожидание игроков: партия создана, второй участник ещё не подключился. */
    WAITING,

    /** Ход игрока, который играет крестиками (значок 1). */
    X_TURN,

    /** Ход игрока, который играет ноликами (значок 2). */
    O_TURN,

    /** Ничья: поле заполнено, победной линии нет. */
    DRAW,

    /** Победа игрока с крестиками (UUID победителя хранится в GameSession.winnerId). */
    WIN_X,

    /** Победа игрока с ноликами (UUID победителя хранится в GameSession.winnerId). */
    WIN_O;

    /**
     * Партия завершена (больше никто не может ходить).
     *
     * @return true для DRAW, WIN_X, WIN_O
     */
    public boolean isFinished() {
        return this == DRAW || this == WIN_X || this == WIN_O;
    }

    /**
     * В текущем состоянии ожидается ход какой-либо стороны.
     *
     * @return true для X_TURN и O_TURN
     */
    public boolean isActive() {
        return this == X_TURN || this == O_TURN;
    }
}
