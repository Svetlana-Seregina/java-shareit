package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerIT {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    private final User userBooker = new User(1L, "Maria", "maria25@yandex.ru");
    private final User userOwner = new User(2L, "Maria", "maria25@yandex.ru");
    private final Item item = new Item(
            1L, "Stairs", "New Stairs", true, userOwner.getId(), 0L);
    private final BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(
            1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
    private final BookingDtoResponse bookingDtoResponse = new BookingDtoResponse(
            1L, bookingDtoRequest.getStart(), bookingDtoRequest.getEnd(),
            BookingState.WAITING, new BookingDtoResponse.User(userBooker.getId()),
            new BookingDtoResponse.Item(item.getId(), item.getName()));
    private final BookingDtoResponse bookingDtoResponseUpdate = new BookingDtoResponse(
            1L, bookingDtoRequest.getStart(), bookingDtoRequest.getEnd(),
            BookingState.APPROVED, new BookingDtoResponse.User(userBooker.getId()),
            new BookingDtoResponse.Item(item.getId(), item.getName()));

    @SneakyThrows
    @Test
    void save() {
        when(bookingService.save(anyLong(), any())).thenReturn(bookingDtoResponse);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoResponse), result);
    }

    @SneakyThrows
    @Test
    void update() {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoResponseUpdate);

        String result = mockMvc.perform(patch("/bookings/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .content(objectMapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoResponseUpdate), result);
    }

    @SneakyThrows
    @Test
    void findById() {
        long bookingId = 0;

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).findById(1L, bookingId);
    }

    @SneakyThrows
    @Test
    void findAll() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "FUTURE"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).findAll(1L, "FUTURE", 0, 20);
    }

    @SneakyThrows
    @Test
    void findAllByOwner() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "PAST"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).findAll(1L, "PAST", 0, 20);
    }

}