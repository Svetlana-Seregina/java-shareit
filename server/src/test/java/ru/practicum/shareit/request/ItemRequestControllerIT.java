package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIT {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void save() {
        ItemRequestDtoCreate itemRequestDtoCreate = new ItemRequestDtoCreate("Need 4 chairs");
        ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse(1L, "Need 4 chairs", LocalDateTime.now());

        when(itemRequestService.save(anyLong(), any())).thenReturn(itemRequestDtoResponse);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemRequestDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDtoResponse), result);

        verify(itemRequestService).save(1L, itemRequestDtoCreate);
    }

    @SneakyThrows
    @Test
    void findAll() {
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemRequestService).findAll(1L);
    }

    @SneakyThrows
    @Test
    void findAllBySize() {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemRequestService).findAllBySize(1L, 0, 20);
    }

    @SneakyThrows
    @Test
    void findById() {
        long requestId = 0;
        mockMvc.perform(get("/requests/{id}", requestId)
                        .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemRequestService).findById(1L, requestId);
    }
}