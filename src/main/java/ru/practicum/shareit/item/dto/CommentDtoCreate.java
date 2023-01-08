package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentDtoCreate {

    private final Long id;
    @NotBlank
    private final String text;

}
