package web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Файл: web/exception/GlobalExceptionHandler.java
 *
 * Назначение файла: единая точка перевода внутренних ошибок приложения
 * в корректные HTTP-коды ответов (чтобы клиент не получал «500 + stacktrace»).
 *
 * @RestControllerAdvice — Spring перехватывает исключения из всех контроллеров
 * и передаёт их в методы, помеченные @ExceptionHandler.
 *
 * Соответствие кодам:
 *  - IllegalArgumentException — «игра не найдена», «ход некорректен», «нет прав» -> 400 Bad Request;
 *  - IllegalStateException   — нарушение порядка (например, повторный ход в завершённой партии) -> 409 Conflict;
 *  - прочие RuntimeException — внутренняя ошибка -> 500 Internal Server Error.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Ожидаемые пользовательские ошибки: неверные данные запроса или состояние игры.
     *
     * @param e исключение с человекочитаемым описанием проблемы
     * @return JSON {"error": "..."} со статусом 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", String.valueOf(e.getMessage())));
    }

    /**
     * Ошибки недопустимого состояния объекта (партия уже завершена, игрок уже подключён).
     *
     * @param e исключение IllegalStatexception
     * @return JSON {"error": "..."} со статусом 409
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", String.valueOf(e.getMessage())));
    }
}
