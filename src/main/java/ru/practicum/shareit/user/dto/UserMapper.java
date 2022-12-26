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
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static User toUpdateUser(UserDto user, UserDto userDto) {
        User user1 = new User();
        user1.setId(user.getId());
        user1.setName(userDto.getName() != null ? userDto.getName() : user.getName());
        user1.setEmail(userDto.getEmail() != null ? userDto.getEmail() : user.getEmail());
        return user1;
    }

    public static User toUpdateUser(Long userId, UserDto userDto) {
        User user = new User();
        user.setId(userId);
        user.setName(userDto.getName() != null ? userDto.getName() : null);
        user.setEmail(userDto.getEmail() != null ? userDto.getEmail() : null);
        return user;
    }

}
