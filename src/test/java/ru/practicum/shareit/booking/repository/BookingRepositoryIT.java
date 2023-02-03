package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryIT {

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    private User userBooker;
    private User userItemOwner;
    private Item item;
    private Booking booking;
    private Pageable sortedByStartDesc;

    @BeforeEach
    void init() {
        userBooker = userRepository.save(new User(null, "Alex", "alex@yandex.ru"));
        userItemOwner = userRepository.save(new User(null, "Pavel", "pavel@yandex.ru"));
        item = itemRepository.save(new Item(null, "Stairs", "New stairs", true, userItemOwner.getId(), null));
        booking = bookingRepository.save(new Booking(
                null, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4), item, userBooker, BookingState.WAITING));
        sortedByStartDesc = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "start"));
    }

    @AfterEach
    void clear() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getAllByBooker_Id() {
        List<Booking> allByBookerId =
                bookingRepository.getAllByBooker_Id(0L, sortedByStartDesc);
        assertTrue(allByBookerId.isEmpty());
    }

    @Test
    void getAllByBooker_IdAndStartIsAfter() {
        List<Booking> bookings = bookingRepository.getAllByBooker_IdAndStartIsAfter(
                userBooker.getId(), LocalDateTime.now(), sortedByStartDesc);

        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
        assertTrue(bookings.get(0).getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    void getAllByBooker_IdAndStatusAndStartIsAfter() {
        List<Booking> bookings = bookingRepository.getAllByBooker_IdAndStartIsAfter(
                userBooker.getId(), LocalDateTime.now(), sortedByStartDesc);

        assertEquals(booking.getStart(), bookings.get(0).getStart());
        assertTrue(bookings.get(0).getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    void getAllByBooker_IdAndStartBeforeAndEndAfter() {
        List<Booking> bookings = bookingRepository.getAllByBooker_IdAndStartBeforeAndEndAfter(
                userBooker.getId(), LocalDateTime.now(), LocalDateTime.now(), sortedByStartDesc);

        assertTrue(bookings.isEmpty());
    }

    @Test
    void getAllByBooker_IdAndStartBeforeAndEndBefore() {
        List<Booking> bookings = bookingRepository.getAllByBooker_IdAndStartBeforeAndEndBefore(
                userBooker.getId(), LocalDateTime.now(), LocalDateTime.now(), sortedByStartDesc);

        assertTrue(bookings.isEmpty());
    }

    @Test
    void getAllByItem_OwnerIdAndStartBeforeAndEndAfter() {
        List<Booking> bookings = bookingRepository.getAllByItem_OwnerIdAndStartBeforeAndEndAfter(
                userItemOwner.getId(), LocalDateTime.now(), LocalDateTime.now(), sortedByStartDesc);

        assertTrue(bookings.isEmpty());
    }

    @Test
    void getAllByItem_OwnerIdAndStartBeforeAndEndBefore() {
        List<Booking> bookings = bookingRepository.getAllByItem_OwnerIdAndStartBeforeAndEndBefore(
                userItemOwner.getId(), LocalDateTime.now(), LocalDateTime.now(), sortedByStartDesc);

        assertTrue(bookings.isEmpty());
    }

    @Test
    void getAllByItem_OwnerId() {
        List<Booking> bookings = bookingRepository.getAllByItem_OwnerId(
                userItemOwner.getId(), sortedByStartDesc);

        assertEquals(1, bookings.size());
    }

    @Test
    void getAllByItem_OwnerIdAndStartIsAfter() {
        List<Booking> bookings = bookingRepository.getAllByItem_OwnerIdAndStartIsAfter(
                userItemOwner.getId(), LocalDateTime.now(), sortedByStartDesc);

        assertEquals(1, bookings.size());
    }

    @Test
    void getAllByItem_OwnerIdAndStatusAndStartIsAfter() {
        List<Booking> bookings = bookingRepository.getAllByItem_OwnerIdAndStatusAndStartIsAfter(
                userItemOwner.getId(), BookingState.WAITING, LocalDateTime.now(), sortedByStartDesc);

        assertEquals(1, bookings.size());
    }

    @Test
    void getAllByItem_IdAndStartIsLessThanEqualOrEndIsLessThanEqualAndStatusIs() {
        List<Booking> bookings = bookingRepository.getAllByItem_IdAndStartIsLessThanEqualOrEndIsLessThanEqualAndStatusIs(
                item.getId(), LocalDateTime.now(), LocalDateTime.now(), BookingState.WAITING);

        assertEquals(0, bookings.size());
    }

    @Test
    void getAllByItem_IdAndStartIsAfterAndStatusIs() {
        List<Booking> bookings = bookingRepository.getAllByItem_IdAndStartIsAfterAndStatusIs(
                item.getId(), LocalDateTime.now(), BookingState.WAITING);

        assertEquals(1, bookings.size());
    }

    @Test
    void getAllByItem_IdAndStartIsBeforeAndEndIsBefore() {
        List<Booking> bookings = bookingRepository.getAllByItem_IdAndStartIsBeforeAndEndIsBefore(
                item.getId(), LocalDateTime.now(), LocalDateTime.now());

        assertEquals(0, bookings.size());
    }

    @Test
    void getAllByItem_InAndStartIsLessThanEqualOrEndIsLessThanEqualAndStatusIs() {
        List<Item> items = List.of(item);

        List<Booking> bookings = bookingRepository.getAllByItem_InAndStartIsLessThanEqualOrEndIsLessThanEqualAndStatusIs(
                items, LocalDateTime.now(), LocalDateTime.now(), BookingState.WAITING);

        assertEquals(0, bookings.size());
    }

    @Test
    void getAllByItem_InAndStartAfterAndStatusIs() {
        List<Item> items = List.of(item);

        List<Booking> bookings = bookingRepository.getAllByItem_InAndStartAfterAndStatusIs(
                items, LocalDateTime.now(), BookingState.WAITING);

        assertEquals(1, bookings.size());
    }
}