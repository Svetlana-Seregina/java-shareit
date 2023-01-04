package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentDto {

    private final Long id;
    @NotBlank
    private final String text;
    private final Item item;
    private final User author;
    private final String authorName;
    private final LocalDateTime created;

}
