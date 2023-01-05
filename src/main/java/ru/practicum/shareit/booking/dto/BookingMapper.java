package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

    /*public static BookingDtoRequest toBookingDtoRequest(Booking booking) {
        return new BookingDtoRequest(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new BookingDtoRequest.Booker(booking.getBooker().getId(), booking.getBooker().getName()),
                new BookingDtoRequest.Item(booking.getItem().getId(), booking.getItem().getName())
        );
    }*/

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                //booking.getBooker(),
                //booking.getItem(),
                new BookingDto.User(booking.getBooker().getId(), booking.getBooker().getName()),
                new BookingDto.Item(booking.getItem().getId(), booking.getItem().getName()),
                booking.getBookerId()
        );
    }

    public static Booking toBooking(User user, Item item, BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setEnd(bookingDto.getEnd());
        booking.setStart(bookingDto.getStart());
        booking.setBooker(user);
        booking.setItem(item);
        item.setRequestId(bookingDto.getId());
        booking.setStatus(BookingState.WAITING);
        return booking;
    }

    public static Booking toUpdateBooking(Booking booking, boolean approved) {
        Booking booking1 = new Booking();
        booking1.setId(booking.getId());
        booking1.setStart(booking.getStart());
        booking1.setEnd(booking.getEnd());
        booking1.setBooker(booking.getBooker());
        booking1.setItem(booking.getItem());
        booking1.setStatus(approved ? BookingState.APPROVED : BookingState.REJECTED);
        return booking1;
    }

}
