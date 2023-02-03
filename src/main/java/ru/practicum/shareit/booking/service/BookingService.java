package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {

    BookingDtoResponse save(long userId, BookingDtoRequest bookingDtoRequest);

    BookingDtoResponse update(long userId, long id, boolean approved);

    BookingDtoResponse findById(long userId, long id);

    List<BookingDtoResponse> findAll(long userId, String state, Integer from, Integer size);

    List<BookingDtoResponse> findAllByOwner(long userId, String state, Integer from, Integer size);

}
