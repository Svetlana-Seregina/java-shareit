package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final BookingState status;
    private final User booker;
    private final Item item;
    private final Long itemId;

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