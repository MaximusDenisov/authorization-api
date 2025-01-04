package ru.krbk.authorization.utils;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.everit.json.schema.ValidationException;
import org.json.JSONObject;
import ru.krbk.authorization.exception.SchemaValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

public class JsonSchemaValidator {

    public static void validate(String schemaFilePath, String jsonMessage) {
        Schema schema = loadSchema(schemaFilePath);
        try {
            schema.validate(new JSONObject(jsonMessage));
        } catch (ValidationException e) {
            String errorMessages = e.getAllMessages().stream()
                    .map(msg -> "- " + msg)
                    .collect(Collectors.joining("\n"));
            throw new SchemaValidationException("Валидация провалена:\n" + errorMessages);
        }
    }

    public static Schema loadSchema(String schemaFilePath) {
        try (InputStream inputStream = JsonSchemaValidator.class.getResourceAsStream(schemaFilePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Schema file not found: " + schemaFilePath);
            }
            JSONObject rawSchema = new JSONObject(new String(inputStream.readAllBytes()));
            return SchemaLoader.load(rawSchema);
        } catch (IOException e) {
            throw new RuntimeException("Error loading schema");
        }
    }

}
