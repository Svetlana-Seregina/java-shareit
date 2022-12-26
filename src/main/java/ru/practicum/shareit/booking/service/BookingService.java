package ru.practicum.shareit.booking.service;

import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.booking.dto.BookingDto;

public interface BookingService {

    BookingDto save(Long userId, BookingDto bookingDto) throws MethodArgumentNotValidException;

    BookingDto update(Long userId, Long id, BookingDto bookingDto);

    BookingDto findById(Long userId, Long id);

}
