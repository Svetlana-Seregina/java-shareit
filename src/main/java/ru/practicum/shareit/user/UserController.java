package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto saveNewUser(@Valid @RequestBody UserDto userDto) {
        log.info("Обрабатываем запрос на создание пользователя: " + userDto);
        return userService.saveNewUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Обрабатываем запрос на обновление пользователя: " + userDto);
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    void deleteUser(@PathVariable long id) {
        log.info("Обрабатываем запрос на удаление пользователя с id = " + id);
        userService.deleteUser(id);
    }

    @GetMapping
    public Collection<UserDto> findAllUsers() {
        log.info("Обрабатываем запрос на получение списка всех пользователей.");
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable Long id) {
        log.info("Обрабатываем запрос на получение пользователя с id = " + id);
        return userService.findUserById(id);
    }

}
