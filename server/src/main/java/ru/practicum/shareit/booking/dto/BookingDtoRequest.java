package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDtoRequest {
    private final Long itemId;
    private final LocalDateTime start;
    private final LocalDateTime end;

}