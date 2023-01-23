package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    BookingService bookingService;
    @InjectMocks
    BookingController bookingController;
    private final User userBooker = new User(1L, "Maria", "maria25@yandex.ru");
    private final User userOwner = new User(2L, "Maria", "maria25@yandex.ru");
    private final Item item = new Item(
            1L, "Stairs", "New Stairs", true, userOwner.getId(), 0L);
    private final BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(
            1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
    private final BookingDtoResponse bookingDtoResponse = new BookingDtoResponse(1L, bookingDtoRequest.getStart(), bookingDtoRequest.getEnd(),
            BookingState.APPROVED, new BookingDtoResponse.User(userBooker.getId()),
            new BookingDtoResponse.Item(item.getId(), item.getName()));


    @Test
    void save() {
        Mockito.when(bookingService.save(userBooker.getId(), bookingDtoRequest)).thenReturn(bookingDtoResponse);

        BookingDtoResponse result = bookingController.save(userBooker.getId(), bookingDtoRequest);

        assertEquals(bookingDtoResponse, result);
    }

    @Test
    void update() {
        Mockito.when(bookingService.update(userOwner.getId(), 1L, true)).thenReturn(bookingDtoResponse);

        BookingDtoResponse result = bookingController.update(userOwner.getId(), 1L, true);

        assertEquals(bookingDtoResponse, result);
    }

    @Test
    void findById() {
        long bookingId = 0;
        Mockito.when(bookingService.findById(userBooker.getId(), bookingId)).thenReturn(bookingDtoResponse);

        BookingDtoResponse result = bookingController.findById(userBooker.getId(), bookingId);

        assertEquals(bookingDtoResponse, result);
    }

    @Test
    void findAll() {
        List<BookingDtoResponse> bookingDtoResponses = List.of(bookingDtoResponse);
        Mockito.when(bookingService.findAll(userBooker.getId(), "FUTURE", 0L, 20)).thenReturn(bookingDtoResponses);

        List<BookingDtoResponse> result = bookingController.findAll(userBooker.getId(), "FUTURE", 0L, 20);

        assertEquals(bookingDtoResponses, result);
    }

    @Test
    void findAllByOwner() {
        List<BookingDtoResponse> bookingDtoResponses = List.of(bookingDtoResponse);
        Mockito.when(bookingService.findAll(userOwner.getId(), "FUTURE", 0L, 20)).thenReturn(bookingDtoResponses);

        List<BookingDtoResponse> result = bookingController.findAll(userOwner.getId(), "FUTURE", 0L, 20);

        assertEquals(bookingDtoResponses, result);
    }
}