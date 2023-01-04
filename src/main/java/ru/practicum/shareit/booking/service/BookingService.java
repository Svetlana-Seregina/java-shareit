package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto save(Long userId, BookingDto bookingDto);

    BookingDto update(Long userId, Long id, String approved);

    BookingDto findById(Long userId, Long id);

    List<BookingDto> findAll(Long userId, String state);

    List<BookingDto> findAllByOwner(Long userId, String state);

}
