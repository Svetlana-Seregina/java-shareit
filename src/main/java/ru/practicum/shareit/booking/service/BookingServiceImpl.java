package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingDto save(Long userId, BookingDto bookingDto) throws MethodArgumentNotValidException {
        ItemDto i = itemService.findById(userId, bookingDto.getItemId());
        LocalDateTime start = bookingDto.getStart();
        log.info("Дата начала бронирования: {}", start);
        LocalDateTime end = bookingDto.getEnd();
        log.info("Дата окончания бронирования: {}", end);
        log.info("Вещь доступна к аренде?: {}", i.getAvailable());
        if((!start.isBefore(end)) || (!i.getAvailable().equals(true))) {
            throw new ValidationException("Вещь не доступна или ошибка в датах start/end аренды.");
        }
            Booking booking = bookingRepository.save(BookingMapper.toBooking(userId, bookingDto));
            return BookingMapper.toBookingDto(booking);
        }

    @Override
    public BookingDto update(Long userId, Long id, BookingDto bookingDto) {
        return null;
    }

    @Override
    public BookingDto findById(Long userId, Long id) {
        return null;
    }


}
