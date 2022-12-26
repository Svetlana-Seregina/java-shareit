package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto save(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody BookingDto bookingDto) throws MethodArgumentNotValidException {
        log.warn("Обрабатываем запрос аренду вещи: {} от пользователя: {}", bookingDto, userId);
        return bookingService.save(userId, bookingDto);
    }
    // /bookings/:bookingId?approved=true
    @PatchMapping("{id}")
    public void update(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable Long id,
                       @Valid @RequestBody BookingDto bookingDto) {
        log.warn("Обрабатываем запрос обновление статуса аренды вещи: {} от пользователя: {}, id аренды: {}", bookingDto, userId, id);
        bookingService.update(userId, id, bookingDto);
    }

    @GetMapping("{id}")
    public BookingDto findById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        log.warn("Обрабатываем запрос на получение аренды вещи по id = {} от пользователя: {}", id, userId);
        return bookingService.findById(userId, id);
    }



}
