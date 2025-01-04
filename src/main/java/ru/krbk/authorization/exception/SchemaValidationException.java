package ru.krbk.authorization.exception;

public class SchemaValidationException extends RuntimeException {
    public SchemaValidationException(String message) {
        super(message);
    }
}