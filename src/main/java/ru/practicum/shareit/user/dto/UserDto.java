package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {

    private final Long id;
    @NotBlank(groups = {Create.class})
    private final String name;
    @NotNull
    @Email(groups = {Create.class, Update.class})
    private final String email;


}
