package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse save(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @RequestBody BookingDtoRequest bookingDtoRequest) {
        log.warn("Обрабатываем запрос аренду вещи: {} от пользователя: {}", bookingDtoRequest, userId);
        return bookingService.save(userId, bookingDtoRequest);
    }

    // PATCH /bookings/:bookingId?approved={approved} (параметр approved может принимать значения true или false)
    @PatchMapping("{id}")
    public BookingDtoResponse update(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long id,
                                     @RequestParam(value = "approved") Boolean approved) {
        log.warn("Обрабатываем запрос на обновление статуса аренды = {} вещи от владельца с id: {}, id аренды: {}",
                approved, userId, id);
        return bookingService.update(userId, id, approved);
    }

    // GET /bookings/{bookingId}
    @GetMapping("{id}")
    public BookingDtoResponse findById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id) {
        log.warn("Обрабатываем запрос на получение аренды вещи по id = {} от владельца вещи или арендатора с id: {}", id, userId);
        return bookingService.findById(userId, id);
    }

    // GET /bookings?state={state}
    @GetMapping
    public List<BookingDtoResponse> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(value = "state", defaultValue = "ALL") String state,
                                            @RequestParam(value = "from", defaultValue = "0") Integer from,
                                            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        log.warn("Обрабатываем запрос на получение списка всех бронирований пользователя: {}, статус бронирования: {}, " +
                "значение from = {}, size = {}", userId, state, from, size);
        return bookingService.findAll(userId, state, from, size);
    }

    // GET /bookings/owner?state={state}
    @GetMapping("/owner")
    public List<BookingDtoResponse> findAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                   @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                   @RequestParam(value = "size", defaultValue = "20") Integer size) {
        log.warn("Обрабатываем запрос на получение списка всех бронирований пользователя: {}, статус бронирования: {}", userId, state);
        return bookingService.findAllByOwner(userId, state, from, size);
    }

}
