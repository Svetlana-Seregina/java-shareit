package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Setter
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
    private Booking lastBooking;
    private Booking nextBooking;
    private Comment[] comments = new Comment[0];

}
