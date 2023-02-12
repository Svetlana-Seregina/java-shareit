package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserClient userClient;

    // saveNew
    @PostMapping
    public ResponseEntity<Object> saveNew(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Обрабатываем запрос на создание пользователя: {}", userDto);
        return userClient.saveNew(userDto);
    }

    // update
    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable long id, @Validated(Update.class) @RequestBody UserDto userDto) {
        log.info("Обрабатываем запрос на обновление пользователя: {}", userDto);
        return userClient.update(id, userDto);
    }

    // deleteById
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable long id) {
        log.info("Обрабатываем запрос на удаление пользователя с id = {}", id);
        return userClient.deleteById(id);
    }

    // findAll
    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Обрабатываем запрос на получение списка всех пользователей.");
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable long id) {
        log.info("Обрабатываем запрос на получение пользователя с id = {}", id);
        return userClient.findById(id);
    }
}