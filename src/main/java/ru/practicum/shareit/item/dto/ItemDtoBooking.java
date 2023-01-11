package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@RequiredArgsConstructor
@Setter
public class ItemDtoBooking {

    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private Booking lastBooking;
    private Booking nextBooking;
    private CommentDtoResponse[] comments = new CommentDtoResponse[0];

    @Data
    public static class Booking {
        private final long id;
        private final long bookerId;
    }


}
