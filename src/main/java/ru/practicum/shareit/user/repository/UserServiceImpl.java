package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto saveNewUser(UserDto userDto) {
        log.info("Создание пользователя.");
        User user = userRepository.saveNewUser(UserMapper.toCreateUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        log.info("Обновление пользователя.");
        User user = userRepository.updateUser(userId, UserMapper.toUpdateUser(userId, userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Удаление пользователя.");
        userRepository.deleteUser(userId);
    }

    @Override
    public Collection<UserDto> findAllUsers() {
        log.info("Получение списка всех пользователей.");
        Collection<User> userList = userRepository.findAllUsers();
        Collection<UserDto> usersDto = new ArrayList<>();
        for (User value : userList) {
            UserDto userDto = UserMapper.toUserDto(value);
            usersDto.add(userDto);
        }
        return usersDto;
    }

    @Override
    public UserDto findUserById(Long id) {
        log.info("Получение пользователя по id = " + id);
        User user = userRepository.findUserById(id);
        return UserMapper.toUserDto(user);
    }
}
