package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class Booking {

    private final Long id;
    @FutureOrPresent
    private final LocalDate start;
    @FutureOrPresent
    private final LocalDate end;
    private final Item item;
    private final User booker;
    private final String status;
    // WAITING - новое бронирование, ожидает одобрения
    // APPROVED - Дополнительные советы ментора2бронирование подтверждено владельцем
    // REJECTED - бронирование отклонено владельцем
    // CANCELED - бронирование отменено создателем
}
