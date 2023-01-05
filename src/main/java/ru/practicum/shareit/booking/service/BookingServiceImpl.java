package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
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
import java.util.ArrayList;
import java.util.Comparator;
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
    public BookingDto save(long userId, BookingDto bookingDto) {
        //var it = itemRepository.findById(bookingDtoRequest.getItem().getId());
        /*Item item = itemRepository.findById(bookingDto.getItem().getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещи с id = %d нет в базе.", bookingDto.getItem().getId())));*/
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещи с id = %d нет в базе.", bookingDto.getItemId())));

        log.info("ВЕЩЬ: {}", item);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", userId)));
        log.info("ПОЛЬЗОВАТЕЛЬ: {}", user);
        validationAvailableAndTime(bookingDto, item);
        if (item.getOwnerId().equals(userId)) {
            throw new EntityNotFoundException("Владелец вещи не может забронировать свою вещь.");
        }
        Booking booking = bookingRepository.save(BookingMapper.toBooking(user, item, bookingDto));
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
        Booking booking1 = BookingMapper.toUpdateBooking(booking, approved);
        Booking booking2 = bookingRepository.save(booking1);
        log.info("BOOKING APPROVED: {}", booking2);
        return BookingMapper.toBookingDto(booking1);
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
        List<Booking> allBookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                allBookings.addAll(bookingRepository.getAllByBooker_Id(userId));
                break;
            case "FUTURE":
                allBookings.addAll(bookingRepository.getAllByBooker_IdAndStartIsAfter(userId, LocalDateTime.now()));
                break;
            case "CURRENT":
                allBookings.addAll(bookingRepository
                        .getAllByBooker_IdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(), LocalDateTime.now()));
                break;
            case "PAST":
                allBookings.addAll(bookingRepository
                        .getAllByBooker_IdAndStartBeforeAndEndBefore(userId, LocalDateTime.now(), LocalDateTime.now()));
                break;
            case "WAITING":
                allBookings.addAll(bookingRepository
                        .getAllByBooker_IdAndStatusAndStartIsAfter(userId, BookingState.WAITING, LocalDateTime.now()));
                break;
            case "REJECTED":
                allBookings.addAll(bookingRepository
                        .getAllByBooker_IdAndStatusAndStartIsAfter(userId, BookingState.REJECTED, LocalDateTime.now()));
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return allBookings.stream()
                .map(BookingMapper::toBookingDto)
                .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByOwner(long userId, String state) {
        userService.findById(userId);
        List<Booking> allBookings = new ArrayList<>();
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Unknown state: " + state);
        }

        switch (bookingState) {
            case ALL:
                allBookings.addAll(bookingRepository.getAllByItem_OwnerId(userId));
                break;
            case FUTURE:
                allBookings.addAll(bookingRepository.getAllByItem_OwnerIdAndStartIsAfter(userId, LocalDateTime.now()));
                break;
            case CURRENT:
                allBookings.addAll(bookingRepository
                        .getAllByItem_OwnerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(), LocalDateTime.now()));
                break;
            case PAST:
                allBookings.addAll(bookingRepository
                        .getAllByItem_OwnerIdAndStartBeforeAndEndBefore(userId, LocalDateTime.now(), LocalDateTime.now()));
                break;
            case WAITING:
                allBookings.addAll(bookingRepository
                        .getAllByItem_OwnerIdAndStatusAndStartIsAfter(userId, BookingState.WAITING, LocalDateTime.now()));
                break;
            case REJECTED:
                allBookings.addAll(bookingRepository
                        .getAllByItem_OwnerIdAndStatusAndStartIsAfter(userId, BookingState.REJECTED, LocalDateTime.now()));
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        log.info("Всего найдено вещей пользователя = {}", allBookings.size());
        log.info("Найдены вещи пользователя: {}", allBookings);

        return allBookings.stream()
                .map(BookingMapper::toBookingDto)
                .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                .collect(Collectors.toList());
    }


    private void validationAvailableAndTime(BookingDto bookingDto, Item item) {
        LocalDateTime start = bookingDto.getStart();
        log.info("Дата начала бронирования: {}", start);
        LocalDateTime end = bookingDto.getEnd();
        log.info("Дата окончания бронирования: {}", end);
        log.info("Вещь доступна к аренде?: {}", item.getAvailable());
        if ((!start.isBefore(end)) || (!item.getAvailable().equals(true))) {
            throw new ValidationException("Вещь не доступна или ошибка в датах start/end аренды.");
        }
    }

}
