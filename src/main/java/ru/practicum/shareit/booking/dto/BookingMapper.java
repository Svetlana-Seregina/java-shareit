package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

@UtilityClass
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                    booking.getId(),
                    booking.getStart(),
                    booking.getEnd(),
                    booking.getItemId(),
                    booking.getBookerId(),
                    booking.getStatus()
            );
    }

    public static Booking toBooking(Long userId, BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setEnd(bookingDto.getEnd());
        booking.setStart(booking.getStart());
        booking.setItemId(bookingDto.getItemId());
        booking.setBookerId(userId);
        booking.setStatus(BookingState.WAITING);
        return booking;
    }


}
