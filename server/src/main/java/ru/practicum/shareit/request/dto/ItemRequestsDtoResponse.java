package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.time.LocalDateTime;

@Data
public class ItemRequestsDtoResponse {

    private final Long id;
    private final String description;
    private final LocalDateTime created;
    private ItemDtoResponse[] items = new ItemDtoResponse[0];
}