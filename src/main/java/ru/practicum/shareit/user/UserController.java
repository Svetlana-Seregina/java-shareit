package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto saveNew(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Обрабатываем запрос на создание пользователя: {}", userDto);
        return userService.save(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @Validated(Update.class) @RequestBody UserDto userDto) {
        log.info("Обрабатываем запрос на обновление пользователя: {}", userDto);
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    boolean deleteById(@PathVariable long id) {
        log.info("Обрабатываем запрос на удаление пользователя с id = {}", id);
        return userService.deleteById(id);
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Обрабатываем запрос на получение списка всех пользователей.");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable long id) {
        log.info("Обрабатываем запрос на получение пользователя с id = {}", id);
        return userService.findById(id);
    }

}
