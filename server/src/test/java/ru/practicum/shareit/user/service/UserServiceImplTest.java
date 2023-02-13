package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositiry.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void save() {
        UserDto userDto = new UserDto(null, "Alex", "alex@yandex.ru");
        User userToSave = UserMapper.toCreateUser(userDto);

        when(userRepository.save(any())).thenReturn(userToSave);

        UserDto userDtoToResponse = userServiceImpl.save(userDto);

        assertEquals(userToSave.getName(), userDtoToResponse.getName());
        assertEquals(userToSave.getId(), userDtoToResponse.getId());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void update() {
        long userId = 0;
        User oldUser = new User(0L, "Alex", "alex@yandex.ru");
        UserDto userDto = new UserDto(null, "Alexandr", null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        UserDto userUpdated = userServiceImpl.update(userId, userDto);

        assertEquals("Alexandr", userUpdated.getName());
        assertEquals("alex@yandex.ru", userUpdated.getEmail());
    }

    @Test
    void deleteById() {
        long userId = 0L;

        boolean userDeleted = userServiceImpl.deleteById(userId);

        assertFalse(userDeleted);

        verify(userRepository).deleteById(0L);
    }

    @Test
    void findAll() {
        List<User> users = new ArrayList<>();
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> userDtosToResponse = userServiceImpl.findAll();

        assertEquals(users.size(), userDtosToResponse.size());

        verify(userRepository).findAll();
    }

    @Test
    void findById() {
        long userId = 0L;
        User expectedUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        UserDto actualUserDto = userServiceImpl.findById(userId);

        assertEquals(expectedUser.getName(), actualUserDto.getName());

        verify(userRepository).findById(userId);
    }

    @Test
    void findById_whenUserNotFound() {
        long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userServiceImpl.findById(userId));

        verify(userRepository).findById(0L);
    }
}