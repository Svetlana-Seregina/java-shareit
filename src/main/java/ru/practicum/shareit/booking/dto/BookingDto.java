package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingState;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
public class BookingDto {

    private final Long id;
    @FutureOrPresent
    private final LocalDateTime start;
    @Future
    private final LocalDateTime end;
    private final BookingState status;
    private final User booker;
    private final Item item;

    @Data
    public static class User {
        private final long id;
        private final String name;
    }

    @Data
    public static class Item {
        private final long id;
        private final String name;
    }

}