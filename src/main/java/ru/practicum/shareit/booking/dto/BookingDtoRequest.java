package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.valid.StartBeforeEndDateValid;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@StartBeforeEndDateValid
public class BookingDtoRequest {

    private final Long itemId;
    @FutureOrPresent
    private final LocalDateTime start;
    @Future
    private final LocalDateTime end;

}
