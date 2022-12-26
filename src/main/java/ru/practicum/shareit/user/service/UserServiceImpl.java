package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto save(UserDto userDto) {
        log.info("Создание пользователя.");
        User user = userRepository.save(UserMapper.toCreateUser(userDto));
        log.info("Создан пользователь  {}", user);
        log.info("Количество пользователей в базе = {}", findAll().size());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        log.info("Обновление пользователя.");
        log.info("Получение пользователя по id = {}", userId);
        UserDto userDtoWithId = findById(userId);
        User user = userRepository.save(UserMapper.toUpdateUser(userDtoWithId, userDto));
        log.info("Пользователь обновлен user = {}", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteById(Long userId) {
        log.info("Удаление пользователя.");
        userRepository.deleteById(userId);
        log.info("Пользователь с id = {} удален.", userId);
    }

    @Override
    public List<UserDto> findAll() {
        log.info("Получение списка всех пользователей.");
        List<User> userList = userRepository.findAll();
        log.info("Количество пользователей в списке = {}", userList.size());
        return userList.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(Long id) {
        log.info("Получение пользователя по id = {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", id)));
        log.info("Найден пользователь по id: {}", user);
        return UserMapper.toUserDto(user);
    }
}
