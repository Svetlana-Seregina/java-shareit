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
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositiry.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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
    void save_whenItemNotAvailable_thenThrowValidationException() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        BookingDtoRequest bookingDtoRequest =
                new BookingDtoRequest(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        Item expectedItem = new Item(0L, "Stairs", "New Stairs",
                false, userOwner.getId(), 0L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userBooker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(expectedItem));

        assertThrows(ValidationException.class,
                () -> bookingServiceImpl.save(userBooker.getId(), bookingDtoRequest));
    }

    @Test
    void save_whenItemOwnerIsBooker_thenThrowEntityNotFoundException() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        BookingDtoRequest bookingDtoRequest =
                new BookingDtoRequest(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        Item expectedItem = new Item(0L, "Stairs", "New Stairs",
                true, userOwner.getId(), 0L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userBooker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(expectedItem));

        assertThrows(EntityNotFoundException.class,
                () -> bookingServiceImpl.save(userOwner.getId(), bookingDtoRequest));
    }

    @Test
    void update_whenAllIsCorrect_thenReturnUserUpdated() {
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
    void update_whenUserNotOwnerItem_thenThrowEntityNotFoundException() {
        User userAnother = new User(5L, "Masha", "maria3@yandex.ru");
        User userItemOwner = new User(0L, "Maria", "maria@yandex.ru");
        Booking booking = new Booking(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(2),
                new Item(0L, "Stairs", "New Stairs", true, userItemOwner.getId(), 0L),
                new User(1L, "Alex", "alex@yandex.ru"), BookingState.WAITING);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class,
                () -> bookingServiceImpl.update(userAnother.getId(), booking.getId(), false));
    }

    @Test
    void update_whenStatusIsApproved_thenThrowValidationException() {
        User userItemOwner = new User(0L, "Maria", "maria@yandex.ru");
        Booking booking = new Booking(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(2),
                new Item(0L, "Stairs", "New Stairs", true, userItemOwner.getId(), 0L),
                new User(1L, "Alex", "alex@yandex.ru"), BookingState.APPROVED);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class,
                () -> bookingServiceImpl.update(userItemOwner.getId(), booking.getId(), true));
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
    void findById_whenUserNotOwnerItemAndNotBooker_thenThrowEntityNotFoundException() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        User userAnother = new User(2L, "Petr", "petr@yandex.ru");
        Item expectedItem = new Item(0L, "Stairs", "New Stairs", true, userOwner.getId(), 0L);
        Booking booking = new Booking(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(2),
                expectedItem, userBooker, BookingState.WAITING);

        when(userRepository.findById(userAnother.getId())).thenReturn(Optional.of(userAnother));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class,
                () -> bookingServiceImpl.findById(userAnother.getId(), booking.getId()));
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

        List<BookingDtoResponse> actualAllBookings = bookingServiceImpl.findAll(userBooker.getId(), "ALL", 0, 20);

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
        when(bookingRepository.getAllByBooker_IdAndStartIsAfter(
                anyLong(), any(), any())).thenReturn(bookings);

        List<BookingDtoResponse> actualAllBookings = bookingServiceImpl.findAll(1L, "FUTURE", 0, 20);

        assertEquals(bookings.size(), actualAllBookings.size());

        verify(bookingRepository).getAllByBooker_IdAndStartIsAfter(anyLong(), any(), any());
    }

    @Test
    void findAll_whenStateIsCurrent_thenReturnCurrentBookingsByBookerId() {
        long userId = 1;
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item item = new Item(0L, "Stairs", "New Stairs",
                true, userOwner.getId(), 0L);
        Booking booking = new Booking(0L, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(4),
                item, userBooker, BookingState.CURRENT);
        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userBooker));
        when(bookingRepository.getAllByBooker_IdAndStartBeforeAndEndAfter(
                anyLong(), any(), any(), any())).thenReturn(bookings);

        List<BookingDtoResponse> actualAllBookings = bookingServiceImpl.findAll(1L, "CURRENT", 0, 20);

        assertEquals(bookings.size(), actualAllBookings.size());

        verify(bookingRepository).getAllByBooker_IdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any());
    }

    @Test
    void findAll_whenStateIsPast_thenReturnPastBookingsByBookerId() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item item = new Item(0L, "Stairs", "New Stairs",
                true, userOwner.getId(), 0L);
        Booking booking = new Booking(0L, LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(2),
                item, userBooker, BookingState.PAST);
        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(userBooker.getId())).thenReturn(Optional.of(userBooker));
        when(bookingRepository.getAllByBooker_IdAndStartBeforeAndEndBefore(anyLong(), any(), any(), any())).thenReturn(bookings);

        List<BookingDtoResponse> actualAllBookings = bookingServiceImpl.findAll(1L, "PAST", 0, 20);

        assertEquals(bookings.size(), actualAllBookings.size());

        verify(bookingRepository).getAllByBooker_IdAndStartBeforeAndEndBefore(anyLong(), any(), any(), any());
    }

    @Test
    void findAll_whenStateIsWaiting_thenReturnWaitingBookingsByBookerId() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item item = new Item(0L, "Stairs", "New Stairs",
                true, userOwner.getId(), 0L);
        Booking booking = new Booking(0L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, userBooker, BookingState.WAITING);
        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(userBooker.getId())).thenReturn(Optional.of(userBooker));
        when(bookingRepository.getAllByBooker_IdAndStatusAndStartIsAfter(anyLong(), any(), any(), any())).thenReturn(bookings);

        List<BookingDtoResponse> actualAllBookings = bookingServiceImpl.findAll(1L, "WAITING", 0, 20);

        assertEquals(bookings.size(), actualAllBookings.size());

        verify(bookingRepository).getAllByBooker_IdAndStatusAndStartIsAfter(anyLong(), any(), any(), any());
    }

    @Test
    void findAll_whenStateIsRejected_thenReturnRejectedBookingsByBookerId() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item item = new Item(0L, "Stairs", "New Stairs",
                true, userOwner.getId(), 0L);
        Booking booking = new Booking(0L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, userBooker, BookingState.REJECTED);
        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(userBooker.getId())).thenReturn(Optional.of(userBooker));
        when(bookingRepository.getAllByBooker_IdAndStatusAndStartIsAfter(anyLong(), any(), any(), any())).thenReturn(bookings);

        List<BookingDtoResponse> actualAllBookings = bookingServiceImpl.findAll(1L, "REJECTED", 0, 20);

        assertEquals(bookings.size(), actualAllBookings.size());

        verify(bookingRepository).getAllByBooker_IdAndStatusAndStartIsAfter(anyLong(), any(), any(), any());
    }

    @Test
    void findAll_whenStateIsUnknown_thenReturnValidationException() {
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");

        when(userRepository.findById(userBooker.getId())).thenReturn(Optional.of(userBooker));

        assertThrows(ValidationException.class,
                () -> bookingServiceImpl.findAll(1L, "Unknown", 0, 20));
    }

    @Test
    void findAllByOwner_whenStateIsAll_thenReturnAllBookingsByOwnerId() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item item = new Item(0L, "Stairs", "New Stairs",
                true, userOwner.getId(), 0L);
        Booking booking = new Booking(0L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, userBooker, BookingState.ALL);
        List<Booking> bookings = List.of(booking);

        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        Pageable sortedByStartDesc =
                PageRequest.of(0, 20, sortByStartDesc);

        when(userRepository.findById(userOwner.getId())).thenReturn(Optional.of(userOwner));
        when(bookingRepository.getAllByItem_OwnerId(userOwner.getId(), sortedByStartDesc)).thenReturn(bookings);

        List<BookingDtoResponse> actualAllBookings = bookingServiceImpl.findAllByOwner(userOwner.getId(), "ALL", 0, 20);

        assertEquals(bookings.size(), actualAllBookings.size());

        verify(bookingRepository).getAllByItem_OwnerId(anyLong(), any());
    }

    @Test
    void findAllByOwner_whenStateIsFuture_thenReturnFutureBookingsByOwnerId() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item item = new Item(0L, "Stairs", "New Stairs",
                true, 0L, 0L);
        Booking booking = new Booking(0L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, userBooker, BookingState.FUTURE);
        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userOwner));
        when(bookingRepository.getAllByItem_OwnerIdAndStartIsAfter(anyLong(), any(), any())).thenReturn(bookings);

        List<BookingDtoResponse> actualAllBookings = bookingServiceImpl.findAllByOwner(0L, "FUTURE", 0, 20);

        assertEquals(bookings.size(), actualAllBookings.size());

        verify(bookingRepository).getAllByItem_OwnerIdAndStartIsAfter(anyLong(), any(), any());
    }

    @Test
    void findAllByOwner_whenStateIsCurrent_thenReturnCurrentBookingsByOwnerId() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item item = new Item(0L, "Stairs", "New Stairs",
                true, userOwner.getId(), 0L);
        Booking booking = new Booking(0L, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(4),
                item, userBooker, BookingState.CURRENT);
        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(userOwner.getId())).thenReturn(Optional.of(userOwner));
        when(bookingRepository.getAllByItem_OwnerIdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any())).thenReturn(bookings);

        List<BookingDtoResponse> actualAllBookings = bookingServiceImpl.findAllByOwner(userOwner.getId(), "CURRENT", 0, 20);

        assertEquals(bookings.size(), actualAllBookings.size());

        verify(bookingRepository).getAllByItem_OwnerIdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any());
    }

    @Test
    void findAllByOwner_whenStateIsPast_thenReturnPastBookingsByOwnerId() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item item = new Item(0L, "Stairs", "New Stairs",
                true, userOwner.getId(), 0L);
        Booking booking = new Booking(0L, LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(2),
                item, userBooker, BookingState.PAST);
        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(userOwner.getId())).thenReturn(Optional.of(userOwner));
        when(bookingRepository.getAllByItem_OwnerIdAndStartBeforeAndEndBefore(anyLong(), any(), any(), any())).thenReturn(bookings);

        List<BookingDtoResponse> actualAllBookings = bookingServiceImpl.findAllByOwner(userOwner.getId(), "PAST", 0, 20);

        assertEquals(bookings.size(), actualAllBookings.size());

        verify(bookingRepository).getAllByItem_OwnerIdAndStartBeforeAndEndBefore(anyLong(), any(), any(), any());
    }

    @Test
    void findAllByOwner_whenStateIsWaiting_thenReturnWaitingBookingsByOwnerId() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item item = new Item(0L, "Stairs", "New Stairs",
                true, userOwner.getId(), 0L);
        Booking booking = new Booking(0L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, userBooker, BookingState.PAST);
        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(userOwner.getId())).thenReturn(Optional.of(userOwner));
        when(bookingRepository.getAllByItem_OwnerIdAndStatusAndStartIsAfter(anyLong(), any(), any(), any())).thenReturn(bookings);

        List<BookingDtoResponse> actualAllBookings = bookingServiceImpl.findAllByOwner(userOwner.getId(), "WAITING", 0, 20);

        assertEquals(bookings.size(), actualAllBookings.size());

        verify(bookingRepository).getAllByItem_OwnerIdAndStatusAndStartIsAfter(anyLong(), any(), any(), any());
    }

    @Test
    void findAllByOwner_whenStateIsRejected_thenReturnRejectedBookingsByOwnerId() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item item = new Item(0L, "Stairs", "New Stairs",
                true, userOwner.getId(), 0L);
        Booking booking = new Booking(0L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, userBooker, BookingState.REJECTED);
        List<Booking> bookings = List.of(booking);

        when(userRepository.findById(userOwner.getId())).thenReturn(Optional.of(userOwner));
        when(bookingRepository.getAllByItem_OwnerIdAndStatusAndStartIsAfter(anyLong(), any(), any(), any())).thenReturn(bookings);

        List<BookingDtoResponse> actualAllBookings = bookingServiceImpl.findAllByOwner(userOwner.getId(), "REJECTED", 0, 20);

        assertEquals(bookings.size(), actualAllBookings.size());

        verify(bookingRepository).getAllByItem_OwnerIdAndStatusAndStartIsAfter(anyLong(), any(), any(), any());
    }

    @Test
    void findAllByOwner_whenStateIsUnknown_thenReturnValidationException() {
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item item = new Item(0L, "Stairs", "New Stairs",
                true, userOwner.getId(), 0L);
        Booking booking = new Booking(0L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                item, userBooker, BookingState.REJECTED);

        when(userRepository.findById(userOwner.getId())).thenReturn(Optional.of(userBooker));

        assertThrows(ValidationException.class,
                () -> bookingServiceImpl.findAllByOwner(0L, "Unknown", 0, 20));
    }
}