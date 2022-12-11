package ru.practicum.shareit.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toCreateUser(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static User toUpdateUser(Long userId, UserDto userDto) {
        return User.builder()
                .id(userId)
                .name(userDto.getName() != null ? userDto.getName() : null)
                .email(userDto.getEmail() != null ? userDto.getEmail() : null)
                .build();
    }

}
