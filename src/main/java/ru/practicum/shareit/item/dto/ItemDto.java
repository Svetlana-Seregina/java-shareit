package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {

    private final Long id;
    @NotBlank
    private final String name;
    @NotBlank
    private final String description;
    @NotNull
    private final Boolean available;
    private final Long ownerId;
    private final Long requestId;

}
