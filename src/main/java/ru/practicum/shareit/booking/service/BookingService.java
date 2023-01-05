package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto save(long userId, BookingDto bookingDtoRequest);

    BookingDto update(long userId, long id, boolean approved);

    BookingDto findById(long userId, long id);

    List<BookingDto> findAll(long userId, String state);

    List<BookingDto> findAllByOwner(long userId, String state);

}
