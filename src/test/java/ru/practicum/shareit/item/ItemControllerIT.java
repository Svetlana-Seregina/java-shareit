package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIT {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    private final ItemDtoRequest itemDtoRequestNew1 = new ItemDtoRequest(1L, "Stairs", "New Stairs",
            true, 1L, 0L);
    private final ItemDtoRequest itemDtoRequestNew2 = new ItemDtoRequest(2L, null, "New Stairs",
            true, 1L, 0L);
    private final ItemDtoResponse itemDtoResponseNew1 = new ItemDtoResponse(1L, "Stairs", "New Stairs",
            true, 0L);
    private final ItemDtoRequest itemDtoRequestUpdate1 = new ItemDtoRequest(
            1L, "Stairs large", "Stairs new large", false, 1L, 0L);
    private final ItemDtoResponse itemDtoResponseUpdate1 = new ItemDtoResponse(
            1L, "Stairs large", "Stairs new large", false, 0L);

    @Test
    void saveItem() throws Exception {
        when(itemService.save(anyLong(), any())).thenReturn(itemDtoResponseNew1);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDtoRequestNew1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDtoResponseNew1), result);

        verify(itemService).save(1L, itemDtoRequestNew1);
    }

    @Test
    void saveItem_whenNameNotValid_thenThrowMethodArgumentNotValidException() throws Exception {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDtoRequestNew2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, never()).save(1L, itemDtoRequestNew1);
    }

    @Test
    void saveItem_whenNotUserIdNotValid_thenThrowEntityNotFoundException() throws Exception {
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDtoRequestNew1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, never()).save(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void update() {
        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(itemDtoResponseUpdate1);

        String result = mockMvc.perform(patch("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDtoRequestUpdate1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDtoResponseUpdate1), result);

        verify(itemService).update(1L, 1L, itemDtoRequestUpdate1);
    }

    @SneakyThrows
    @Test
    void findById() {
        long itemId = 0;

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).findById(1L, itemId);
    }

    @SneakyThrows
    @Test
    void findAll() {
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).findAll(1L, 0, 20);
    }

    @SneakyThrows
    @Test
    void searchAllByRequestText() {
        mockMvc.perform(get("/items/search")
                        .param("text", "StAirS")
                        .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).search(1L, "StAirS", 0, 20);
    }

    @SneakyThrows
    @Test
    void saveComment() {
        CommentDtoResponse commentDtoResponse = new CommentDtoResponse(1L, "Really good stairs",
                "Alex", LocalDateTime.now());
        CommentDtoCreate commentDtoCreate = new CommentDtoCreate("Really good stairs");
        when(itemService.save(anyLong(), anyLong(), any())).thenReturn(commentDtoResponse);

        String result = mockMvc.perform(post("/items/{id}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDtoResponse), result);

        verify(itemService).save(1L, 1L, commentDtoCreate);
    }
}