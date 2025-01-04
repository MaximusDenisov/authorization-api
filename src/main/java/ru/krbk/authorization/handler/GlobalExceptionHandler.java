package ru.krbk.authorization.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.krbk.authorization.exception.SchemaValidationException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SchemaValidationException.class)
    public ResponseEntity<Object> handleSchemaValidationException(SchemaValidationException ex) {
        // Формируем тело ответа
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Validation Error",
                        "message", ex.getMessage()
                )
        );
    }
}