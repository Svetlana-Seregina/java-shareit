package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto save(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    void deleteById(Long userId);

    List<UserDto> findAll();

    UserDto findById(Long id);
}
