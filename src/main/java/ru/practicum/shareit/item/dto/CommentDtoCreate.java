package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CommentDtoCreate {

    private final Long id;
    @NotBlank
    private final String text;
    private final String authorName;
    @NotNull
    private final LocalDateTime created;

}
