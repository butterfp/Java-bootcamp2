package domain.model;

import java.util.Arrays;

/**
 * Файл: domain/model/GameBoard.java
 *
 * Назначение файла: доменная модель игрового поля «Крестики-нолики» (3x3)
 * и связанные с полем бизнес-правила.
 *
 * Кодировка клеток:
 * 0 — клетка свободна;
 * 1 — клетка занята крестиками (X);
 * 2 — клетка занята ноликами (O).
 *
 * Это record: компилятор сам создаёт private final поля, канонический конструктор,
 * доступ по именам полей (board()), equals(), hashCode(), toString().
 * Дополнительно здесь объявлены константы значков и методы проверки хода,
 * чтобы правила поля были в одном месте и не дублировались в сервисе.
 */
public record GameBoard(int[][] board) {

    /** Размер стороны поля (классическое 3x3). */
    public static final int SIZE = 3;

    /** Значок пустой клетки. */
    public static final int EMPTY = 0;

    /** Значок крестиков (X) — всегда ходит первым. */
    public static final int X = 1;

    /** Значок ноликов (O). */
    public static final int O = 2;

    /**
     * Компактный конструктор record.
     * Проверяет, что передано поле корректного размера, иначе объект невозможен.
     */
    public GameBoard {
        if (board == null || board.length != SIZE) {
            throw new IllegalArgumentException("Поле должно быть квадратным " + SIZE + "x" + SIZE);
        }
    }

    /**
     * Конструктор «пустого» поля: используется при создании новой партии.
     */
    public GameBoard() {
        this(new int[SIZE][SIZE]);
    }

    /**
     * Возвращает значок, противоположный переданному (X <-> O).
     *
     * @param mark значок 1 или 2
     * @return другой игровой значок
     */
    public static int opponentOf(int mark) {
        return mark == X ? O : X;
    }

    /**
     * Есть ли на поле хотя бы одна свободная клетка.
     *
     * @return true, если партию ещё можно продолжать
     */
    public boolean hasEmptyCell() {
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Проверка корректности хода (задание 2 курса + улучшение из задания 3):
     * - нельзя менять уже занятые клетки;
     * - добавляется ровно один знак;
     * - количество X и O различается не более чем на единицу (ход идёт по очереди).
     *
     * @param updatedBoard поле, которое клиент прислал после своего хода
     * @return true, если переход «старое поле -> новое поле» легален
     */
    public boolean isValidMove(GameBoard updatedBoard) {
        int oldX = 0, oldO = 0;
        int newX = 0, newY = 0; // newY — счётчик ноликов на новом поле

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int oldValue = board[i][j];
                int newValue = updatedBoard.board()[i][j];

                // Занятую клетку переигрывать нельзя
                if (oldValue != EMPTY && oldValue != newValue) {
                    return false;
                }
                if (newValue == X) newX++;
                if (newValue == O) newY++;
                if (oldValue == X) oldX++;
                if (oldValue == O) oldO++;
            }
        }
        // Ровно один новый знак и соблюдение очерёдности ходов
        return (newX + newY) == (oldX + oldO + 1)
                && Math.abs(newX - newY) <= 1;
    }

    /**
     * Проверка победной линии для указанного значка: строки, столбцы, две диагонали.
     *
     * @param mark проверяемый значок (1 или 2)
     * @return true, если у значка есть собранная линия из трёх клеток
     */
    public boolean isWinner(int mark) {
        for (int i = 0; i < SIZE; i++) {
            if (board[i][0] == mark && board[i][1] == mark && board[i][2] == mark) {
                return true; // строка
            }
            if (board[0][i] == mark && board[1][i] == mark && board[2][i] == mark) {
                return true; // столбец
            }
        }
        return (board[0][0] == mark && board[1][1] == mark && board[2][2] == mark)   // главная диагональ
                || (board[0][2] == mark && board[1][1] == mark && board[2][0] == mark); // побочная диагональ
    }

    /**
     * Человекочитаемое представление поля для логов и отладки.
     */
    @Override
    public String toString() {
        return Arrays.deepToString(board);
    }
}
