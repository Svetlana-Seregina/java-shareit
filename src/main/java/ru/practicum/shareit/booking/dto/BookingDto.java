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
    private final Long itemId;
    private final Long bookerId;
    private final BookingState status;

}
