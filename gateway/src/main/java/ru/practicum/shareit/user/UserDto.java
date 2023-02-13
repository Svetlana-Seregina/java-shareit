package ru.practicum.shareit.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
public class UserDto {

    private final Long id;
    @NotBlank(groups = {Create.class})
    private final String name;
    @NotBlank(groups = {Create.class})
    @Email(groups = {Create.class, Update.class})
    private final String email;

}
