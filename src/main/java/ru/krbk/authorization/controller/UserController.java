package ru.krbk.authorization.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.krbk.authorization.entity.IntegrationLog;
import ru.krbk.authorization.entity.User;
import ru.krbk.authorization.repository.IntegrationLogRepository;
import ru.krbk.authorization.repository.UserRepository;
import ru.krbk.authorization.utils.JsonSchemaValidator;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;
    private final IntegrationLogRepository integrationLogRepository;

    public UserController(
            UserRepository userRepository,
            IntegrationLogRepository integrationLogRepository) {
        this.userRepository = userRepository;
        this.integrationLogRepository = integrationLogRepository;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        JsonSchemaValidator.validate("/templates/user-schema.json", user.toString());
        if (user.getAge() < 18 || user.getId() > 100) {
            return ResponseEntity.badRequest().body("Валидация провалена:\nВозраст клиента должен быть от 18 до 100");
        }
        if (userRepository.findAll().stream().noneMatch(someUser -> user.getEmail().equals(someUser.getEmail()))) {
            try {
                logger.info(user.toString());
                userRepository.save(user);
                String successMessage = "Валидация пройдена успешно, клиент создан под ID = " + user.getId();
                saveIntegrationLog(user, successMessage, 200);
                return ResponseEntity.ok(successMessage);
            } catch (RuntimeException e) {
                saveIntegrationLog(user, e);
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            return ResponseEntity.badRequest().body("Такой пользователь уже зарегистрирован!");
        }
    }

    // todo: допилить patch
    @PatchMapping()
    public ResponseEntity<User> patchUser(@RequestBody User user) {
        if (userRepository.findById(user.getId()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(userRepository.save(user));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            String successMessage = String.format("Пользователь с ID = %d удалён", id);
            saveIntegrationLog(
                    String.format("Был введён ID = %d для удаления, удаление прошло успешно", id),
                    "",
                    200
            );
            return ResponseEntity.ok(successMessage);
        } else {
            String failMessage = String.format("Пользователь с указанным ID (%d) не найден", id);
            saveIntegrationLog(
                    String.format("Был введён ID = %d для удаления, удаление не прошло", id),
                    failMessage,
                    400
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Пользователь с указанным ID (%d) не найден", id));
        }

    }

    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteAllUser(@RequestBody String inputCommand) {
        String command = new JSONObject(inputCommand).get("command").toString();
        if (command.equals("deleteAll")) {
            userRepository.deleteAll();
            return ResponseEntity.ok().body("Все объекты внутри таблицы users - удалены!");
        } else {
            return ResponseEntity.badRequest().body("Команда для удаления не распознана!");
        }
    }

    private IntegrationLog saveIntegrationLog(Object obj, Exception exc) {
        return integrationLogRepository.save(new IntegrationLog(
                obj.toString(),
                exc.getMessage(),
                400
        ));
    }

    private IntegrationLog saveIntegrationLog(Object obj, String message, int code) {
        return integrationLogRepository.save(new IntegrationLog(
                obj.toString(),
                message,
                code
        ));
    }
}
