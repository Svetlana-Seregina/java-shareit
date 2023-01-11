package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto save(UserDto userDto);

    UserDto update(long userId, UserDto userDto);

    void deleteById(long userId);

    List<UserDto> findAll();

    UserDto findById(long id);
}
