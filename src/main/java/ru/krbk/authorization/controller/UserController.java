package ru.krbk.authorization.controller;

import org.apache.coyote.Response;
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
        try {
            logger.info(user.toString());
            JsonSchemaValidator.validate("/templates/user-schema.json", user.toString());
            // Если валидация успешна
            userRepository.save(user);
            String successMessage = "Валидация пройдена успешно, клиент создан под ID = " + user.getId();
            integrationLogRepository.save(new IntegrationLog(
                    user.toString(),
                    successMessage,
                    200
            ));
            return ResponseEntity.ok(successMessage);
        } catch (RuntimeException e) {
            // Обработка ошибок валидации
            integrationLogRepository.save(new IntegrationLog(
                    user.toString(),
                    e.getMessage(),
                    400
            ));
            if(user.getAge() < 18 || user.getId() > 100) {
                return ResponseEntity.badRequest().body("Валидация провалена:\nВозраст клиента должен быть от 18 до 100");
            } else {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    }

    // todo: допилить patch
    @PatchMapping()
    public ResponseEntity<User> patchUser(@RequestBody User user) {
        if(userRepository.findById(user.getId()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(userRepository.save(user));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        if(userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            String successMessage = String.format("Пользователь с ID = %d удалён", id);
            integrationLogRepository.save(new IntegrationLog(
                    String.format("Был введён ID = %d для удаления, удаление прошло успешно", id),
                    "",
                    200
            ));
            return ResponseEntity.ok(successMessage);
        } else {
            String failMessage = String.format("Пользователь с указанным ID (%d) не найден", id);
            integrationLogRepository.save(new IntegrationLog(
                    String.format("Был введён ID = %d для удаления, удаление не прошло", id),
                    failMessage,
                    400
            ));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Пользователь с указанным ID (%d) не найден", id));
        }

    }
}
