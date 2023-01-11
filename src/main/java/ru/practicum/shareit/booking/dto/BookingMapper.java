package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new BookingDto.User(booking.getBooker().getId(), booking.getBooker().getName()),
                new BookingDto.Item(booking.getItem().getId(), booking.getItem().getName())
        );
    }

    public static Booking toBooking(User user, Item item, BookingDtoRequest bookingDtoRequest) {
        Booking booking = new Booking();
        booking.setEnd(bookingDtoRequest.getEnd());
        booking.setStart(bookingDtoRequest.getStart());
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingState.WAITING);
        return booking;
    }

    public static BookingDtoResponse toBookingDtoResponse(Booking booking) {
        return new BookingDtoResponse(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new BookingDtoResponse.User(booking.getBooker().getId()),
                new BookingDtoResponse.Item(booking.getItem().getId(), booking.getItem().getName())
        );
    }

}
