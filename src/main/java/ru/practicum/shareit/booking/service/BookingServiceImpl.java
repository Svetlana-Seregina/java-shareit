package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public BookingDto save(long userId, BookingDtoRequest bookingDtoRequest) {
        Item item = itemRepository.findById(bookingDtoRequest.getItemId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещи с id = %d нет в базе.", bookingDtoRequest.getItemId())));
        log.info("ВЕЩЬ: {}", item);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", userId)));
        log.info("ПОЛЬЗОВАТЕЛЬ: {}", user);
        log.info("Вещь доступна к аренде?: {}", item.getAvailable());
        if (!item.getAvailable().equals(true)) {
            throw new ValidationException("Вещь не доступна к аренде.");
        }
        if (item.getOwnerId().equals(userId)) {
            throw new EntityNotFoundException("Владелец вещи не может забронировать свою вещь.");
        }
        Booking booking = bookingRepository.save(BookingMapper.toBooking(user, item, bookingDtoRequest));
        log.info("СОЗДАН ЗАПРОС НА АРЕНДУ С id: {}, статус: {}", booking.getId(), booking.getStatus());
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto update(long userId, long id, boolean approved) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Бронирования с id = %d нет в базе.", id)));
        log.info("Найдено бронирование: {}", booking);
        if (!(booking.getItem().getOwnerId().equals(userId)) && (booking.getBooker().getId().equals(userId))) {
            throw new EntityNotFoundException("У вещи другой владелец: " + userId);
        } else if (booking.getStatus().equals(BookingState.APPROVED)) {
            throw new ValidationException("Статус уже изменен на: APPROVED.");
        }
        booking.setStatus(approved ? BookingState.APPROVED : BookingState.REJECTED);
        log.info("BOOKING APPROVED: {}", booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto findById(long userId, long id) { // bookerId or ItemOwnerId
        userService.findById(userId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Бронирования с id = %d нет в базе.", id)));
        log.info("НАЙДЕНО БРОНИРОВАНИЕ: {}", booking);
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwnerId().equals(userId))) {
            throw new EntityNotFoundException("Бронирование не может быть получено не владельцем вещи или не автором бронирования");
        }
        log.info("Бронирование получено.");
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAll(long userId, String state) {
        userService.findById(userId);
        List<Booking> allBookings;
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Unknown state: " + state);
        }
        switch (bookingState) {
            case ALL:
                allBookings = bookingRepository.getAllByBooker_IdOrderByStartDesc(userId);
                break;
            case FUTURE:
                allBookings = bookingRepository.getAllByBooker_IdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case CURRENT:
                allBookings = bookingRepository
                        .getAllByBooker_IdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                allBookings = bookingRepository
                        .getAllByBooker_IdAndStartBeforeAndEndBefore(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case WAITING:
                allBookings = bookingRepository
                        .getAllByBooker_IdAndStatusAndStartIsAfter(userId, BookingState.WAITING, LocalDateTime.now());
                break;
            case REJECTED:
                allBookings = bookingRepository
                        .getAllByBooker_IdAndStatusAndStartIsAfter(userId, BookingState.REJECTED, LocalDateTime.now());
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return allBookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByOwner(long userId, String state) {
        userService.findById(userId);
        List<Booking> allBookings;
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Unknown state: " + state);
        }

        switch (bookingState) {
            case ALL:
                allBookings = bookingRepository.getAllByItem_OwnerIdOrderByStartDesc(userId);
                break;
            case FUTURE:
                allBookings = bookingRepository.getAllByItem_OwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case CURRENT:
                allBookings = bookingRepository
                        .getAllByItem_OwnerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                allBookings = bookingRepository
                        .getAllByItem_OwnerIdAndStartBeforeAndEndBefore(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case WAITING:
                allBookings = bookingRepository
                        .getAllByItem_OwnerIdAndStatusAndStartIsAfter(userId, BookingState.WAITING, LocalDateTime.now());
                break;
            case REJECTED:
                allBookings = bookingRepository
                        .getAllByItem_OwnerIdAndStatusAndStartIsAfter(userId, BookingState.REJECTED, LocalDateTime.now());
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        log.info("Всего найдено вещей пользователя = {}", allBookings.size());
        log.info("Найдены вещи пользователя: {}", allBookings);

        return allBookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

}
