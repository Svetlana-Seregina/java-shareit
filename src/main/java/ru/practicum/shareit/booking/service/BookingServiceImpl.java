package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");

    @Transactional
    @Override
    public BookingDtoResponse save(long userId, BookingDtoRequest bookingDtoRequest) {
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
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Transactional
    @Override
    public BookingDtoResponse update(long userId, long id, boolean approved) {
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
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public BookingDtoResponse findById(long userId, long id) { // bookerId or ItemOwnerId
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", userId)));
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Бронирования с id = %d нет в базе.", id)));
        log.info("НАЙДЕНО БРОНИРОВАНИЕ: {}", booking);
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwnerId().equals(userId))) {
            throw new EntityNotFoundException("Бронирование не может быть получено не владельцем вещи или не автором бронирования");
        }
        log.info("Бронирование получено.");
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public List<BookingDtoResponse> findAll(long userId, String state, Long from, Integer size) {
        BookingState bookingState = validationUserAndState(userId, state);
        List<Booking> allBookings;
        switch (bookingState) {
            case ALL:
                allBookings = bookingRepository.getAllByBooker_Id(userId, sortByStartDesc)
                        .stream()
                        .skip(from)
                        .limit(size)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                allBookings = bookingRepository.getAllByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), sortByStartDesc);
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
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoResponse> findAllByOwner(long userId, String state, Integer from, Integer size) {
        BookingState bookingState = validationUserAndState(userId, state);
        List<Booking> allBookings;
        Pageable sortedByStartDesc =
                PageRequest.of(from, size, sortByStartDesc);
        switch (bookingState) {
            case ALL:
                allBookings = bookingRepository.getAllByItem_OwnerId(userId, sortedByStartDesc)
                        .stream()
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                allBookings = bookingRepository.getAllByItem_OwnerIdAndStartIsAfter(userId, LocalDateTime.now(), sortByStartDesc);
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
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    private BookingState validationUserAndState(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", userId)));
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Unknown state: " + state);
        }
        return bookingState;
    }

}
