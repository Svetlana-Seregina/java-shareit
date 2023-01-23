package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingServiceImpl;

    @Test
    void save() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        BookingDtoRequest bookingDtoRequest =
                new BookingDtoRequest(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        Item expectedItem = new Item(0L, "Stairs", "New Stairs",
                true, userOwner.getId(), 0L);
        Booking booking = new Booking(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(2),
                expectedItem, userBooker, BookingState.WAITING);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userBooker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(expectedItem));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDtoResponse bookingDtoResponse = bookingServiceImpl.save(userBooker.getId(), bookingDtoRequest);

        assertEquals("Alex", booking.getBooker().getName());
        assertEquals(bookingDtoResponse.getItem().getName(), booking.getItem().getName());

        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void update() {
        long userItemOwnerId = 0;
        long bookingId = 0;
        User userItemOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item expectedItem = new Item(0L, "Stairs", "New Stairs",
                true, userItemOwner.getId(), 0L);
        Booking booking = new Booking(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(2),
                expectedItem, userBooker, BookingState.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDtoResponse bookingDtoResponse = bookingServiceImpl.update(userItemOwnerId, bookingId, false);

        assertEquals(1, bookingDtoResponse.getBooker().getId());
        assertEquals("Stairs", bookingDtoResponse.getItem().getName());
        assertEquals(BookingState.REJECTED, bookingDtoResponse.getStatus());
    }

    @Test
    void findById() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item expectedItem = new Item(0L, "Stairs", "New Stairs",
                true, userOwner.getId(), 0L);
        Booking booking = new Booking(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(2),
                expectedItem, userBooker, BookingState.WAITING);

        when(userRepository.findById(userBooker.getId())).thenReturn(Optional.of(userBooker));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDtoResponse bookingDtoResponse = bookingServiceImpl.findById(userBooker.getId(), booking.getId());

        assertEquals("Stairs", bookingDtoResponse.getItem().getName());
        assertEquals(booking.getEnd(), bookingDtoResponse.getEnd());

        verify(bookingRepository).findById(anyLong());
    }

    @Test
    void findAll_whenStateIsAll_thenReturnAllBookingsByBookerId() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item item = new Item(0L, "Stairs", "New Stairs",
                true, userOwner.getId(), 0L);
        Booking booking = new Booking(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(2),
                item, userBooker, BookingState.WAITING);
        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(userBooker.getId())).thenReturn(Optional.of(userBooker));
        when(bookingRepository.getAllByBooker_Id(anyLong(), any())).thenReturn(bookings);

        List<BookingDtoResponse> actualAllBookings = bookingServiceImpl.findAll(userBooker.getId(), "ALL", 0L, 20);

        assertEquals(bookings.size(), actualAllBookings.size());

        verify(bookingRepository).getAllByBooker_Id(anyLong(), any());
    }

    @Test
    void findAll_whenStateIsFuture_thenReturnFutureBookingsByBookerId() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item item = new Item(0L, "Stairs", "New Stairs",
                true, userOwner.getId(), 0L);
        Booking booking = new Booking(0L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, userBooker, BookingState.FUTURE);
        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(userBooker.getId())).thenReturn(Optional.of(userBooker));
        when(bookingRepository.getAllByBooker_IdAndStartIsAfter(anyLong(), any(), any())).thenReturn(bookings);

        List<BookingDtoResponse> actualAllBookings = bookingServiceImpl.findAll(userBooker.getId(), "FUTURE", 0L, 20);

        assertEquals(bookings.size(), actualAllBookings.size());

        verify(bookingRepository).getAllByBooker_IdAndStartIsAfter(anyLong(), any(), any());
    }

    @Test
    void findAllByOwner_whenStateIsFuture_thenReturnFutureBookingsByOwnerId() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item item = new Item(0L, "Stairs", "New Stairs",
                true, userOwner.getId(), 0L);
        Booking booking = new Booking(0L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, userBooker, BookingState.FUTURE);
        List<Booking> bookings = List.of(booking);
        Page<Booking> bookingPage = new PageImpl<>(bookings);

        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        Pageable sortedByStartDesc =
                PageRequest.of(0, 20, sortByStartDesc);

        when(userRepository.findById(userOwner.getId())).thenReturn(Optional.of(userOwner));
        when(bookingRepository.getAllByItem_OwnerId(userOwner.getId(), sortedByStartDesc)).thenReturn(bookingPage);

        List<BookingDtoResponse> actualAllBookings = bookingServiceImpl.findAllByOwner(userOwner.getId(), "ALL", 0, 20);

        assertEquals(bookings.size(), actualAllBookings.size());

        verify(bookingRepository).getAllByItem_OwnerId(anyLong(), any());
    }

    @Test
    void findAllByOwner_whenStateIsCurrent_thenReturnCurrentBookingsByOwnerId() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item item = new Item(0L, "Stairs", "New Stairs",
                true, userOwner.getId(), 0L);
        Booking booking = new Booking(0L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, userBooker, BookingState.CURRENT);
        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(userOwner.getId())).thenReturn(Optional.of(userOwner));
        when(bookingRepository.getAllByItem_OwnerIdAndStartBeforeAndEndAfter(anyLong(), any(), any())).thenReturn(bookings);

        List<BookingDtoResponse> actualAllBookings = bookingServiceImpl.findAllByOwner(userOwner.getId(), "CURRENT", 0, 20);

        assertEquals(bookings.size(), actualAllBookings.size());

        verify(bookingRepository).getAllByItem_OwnerIdAndStartBeforeAndEndAfter(anyLong(), any(), any());
    }
}