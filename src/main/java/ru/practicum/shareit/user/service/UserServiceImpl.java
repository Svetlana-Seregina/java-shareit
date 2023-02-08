package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto save(UserDto userDto) {
        log.info("Создание пользователя.");
        User user = userRepository.save(UserMapper.toCreateUser(userDto));
        log.info("Создан пользователь  {}", user);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto update(long userId, UserDto userDto) {
        log.info("Обновление пользователя.");
        log.info("Получение пользователя по id = {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", userId)));
        user.setName(userDto.getName() != null && !userDto.getName().isBlank() ? userDto.getName() : user.getName());
        user.setEmail(userDto.getEmail() != null && !userDto.getEmail().isBlank() ? userDto.getEmail() : user.getEmail());
        log.info("Пользователь обновлен user = {}", user);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public boolean deleteById(long userId) {
        log.info("Удаление пользователя.");
        userRepository.deleteById(userId);
        log.info("Пользователь с id = {} удален.", userId);
        return userRepository.existsById(userId);
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
    public UserDto findById(long id) {
        log.info("Получение пользователя по id = {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", id)));
        log.info("Найден пользователь по id: {}", user);
        return UserMapper.toUserDto(user);
    }
}
