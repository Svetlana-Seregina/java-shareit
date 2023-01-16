package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestDtoResponse {


    private final Long id;
    private final String description;
    private final LocalDateTime created;

}
