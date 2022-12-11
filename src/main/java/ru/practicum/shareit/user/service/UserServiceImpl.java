package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto saveNew(UserDto userDto) {
        log.info("Создание пользователя.");
        User user = userRepository.saveNew(UserMapper.toCreateUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        log.info("Обновление пользователя.");
        User user = userRepository.update(userId, UserMapper.toUpdateUser(userId, userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteById(Long userId) {
        log.info("Удаление пользователя.");
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> findAll() {
        log.info("Получение списка всех пользователей.");
        Collection<User> userList = userRepository.findAll();
        return userList.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(Long id) {
        log.info("Получение пользователя по id = {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с id = " + id + " нет в базе."));
        return UserMapper.toUserDto(user);
    }
}
