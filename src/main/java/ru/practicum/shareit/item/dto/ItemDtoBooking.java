package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
@Setter
public class ItemDtoBooking {

    private final Long id;
    @NotBlank
    private final String name;
    @NotBlank
    private final String description;
    @NotNull
    private final Boolean available;
    private Booking lastBooking;
    private Booking nextBooking;
    private CommentDtoCreate[] comments = new CommentDtoCreate[0];

    @Data
    public static class Booking {
        private final long id;
        private final long bookerId;
    }


}
