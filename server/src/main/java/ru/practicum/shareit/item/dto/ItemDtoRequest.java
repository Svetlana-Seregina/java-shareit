package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@RequiredArgsConstructor
@Setter
public class ItemDtoRequest {

    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final Long ownerId;
    private final Long requestId;

}
