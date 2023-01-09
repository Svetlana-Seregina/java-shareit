package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;

@Data
public class BookingDtoResponse {

    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final BookingState status;
    private final User booker;
    private final Item item;

    @Data
    public static class User {
        private final long id;
    }

    @Data
    public static class Item {
        private final long id;
        private final String name;
    }

}
