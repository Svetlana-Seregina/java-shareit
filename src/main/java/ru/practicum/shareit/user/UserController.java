package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    @Validated(Create.class)
    public UserDto saveNew(@Valid @RequestBody UserDto userDto) {
        log.info("Обрабатываем запрос на создание пользователя: " + userDto);
        return userService.saveNew(userDto);
    }

    @PatchMapping("/{id}")
    @Validated(Update.class)
    public UserDto update(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Обрабатываем запрос на обновление пользователя: " + userDto);
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    void deleteById(@PathVariable long id) {
        log.info("Обрабатываем запрос на удаление пользователя с id = " + id);
        userService.deleteById(id);
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Обрабатываем запрос на получение списка всех пользователей.");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable Long id) {
        log.info("Обрабатываем запрос на получение пользователя с id = " + id);
        return userService.findById(id);
    }

}
