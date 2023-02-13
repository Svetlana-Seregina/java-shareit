package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    UserService userService;
    @InjectMocks
    UserController userController;

    @Test
    void saveNew_whenInvoked_thenResponseWithNewUserDto() {
        UserDto userDto = new UserDto(1L, "Alex", "alex@yandex.ru");
        Mockito.when(userService.save(userDto)).thenReturn(userDto);

        UserDto result = userController.saveNew(userDto);

        assertEquals(userDto, result);
    }

    @Test
    void update_whenInvoked_thenResponseWithUpdatedUserDto() {
        UserDto userDtoToUpdate = new UserDto(null, null, "alex89@yandex.ru");
        UserDto userDtoUpdated = new UserDto(0L, "Alex", "alex89@yandex.ru");
        Mockito.when(userService.update(0L, userDtoToUpdate)).thenReturn(userDtoUpdated);

        UserDto result = userController.update(0L, userDtoToUpdate);

        assertEquals(userDtoUpdated, result);

    }

    @Test
    void deleteById_whenInvoked_thenResponseBooleanIsTrue() {
        long userId = 0;
        Mockito.when(userService.deleteById(userId)).thenReturn(true);

        boolean result = userController.deleteById(userId);

        assertTrue(result);
    }

    @Test
    void findAll_whenInvoked_thenResponseWithUserDtoCollection() {
        List<UserDto> expectedUserDto = List.of(new UserDto(1L, "Alex", "alex@yandex.ru"));
        Mockito.when(userService.findAll()).thenReturn(expectedUserDto);

        List<UserDto> response = userController.findAll();

        assertEquals(expectedUserDto, response);
    }

    @Test
    void findById_whenInvoked_thenResponseWithUserDto() {
        long userId = 0;
        UserDto userDto = new UserDto(0L, "Alex", "alex@yandex.ru");
        Mockito.when(userService.findById(userId)).thenReturn(userDto);

        UserDto response = userController.findById(userId);

        assertEquals(userDto, response);
    }
}