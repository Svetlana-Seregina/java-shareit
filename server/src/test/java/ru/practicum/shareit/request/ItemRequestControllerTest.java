package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestsDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    ItemRequestService itemRequestService;
    @InjectMocks
    ItemRequestController itemRequestController;

    ItemRequestDtoCreate itemRequestDtoCreate = new ItemRequestDtoCreate("Need 4 chairs");
    ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse(1L, "Need 4 chairs", LocalDateTime.now());
    ItemRequestsDtoResponse itemRequestsDtoResponse = new ItemRequestsDtoResponse(1L, "Need 5 chairs", LocalDateTime.now());

    @Test
    void save() {
        Mockito.when(itemRequestService.save(1L, itemRequestDtoCreate)).thenReturn(itemRequestDtoResponse);

        ItemRequestDtoResponse result = itemRequestController.save(1L, itemRequestDtoCreate);

        assertEquals(itemRequestDtoResponse, result);
    }

    @Test
    void findAll() {
        List<ItemRequestsDtoResponse> itemRequestsDtoResponses = List.of(itemRequestsDtoResponse);
        Mockito.when(itemRequestService.findAll(1L)).thenReturn(itemRequestsDtoResponses);

        List<ItemRequestsDtoResponse> result = itemRequestController.findAll(1L);

        assertEquals(itemRequestsDtoResponses, result);

    }

    @Test
    void findAllBySize() {
        List<ItemRequestsDtoResponse> itemRequestsDtoResponses = List.of(itemRequestsDtoResponse);
        Mockito.when(itemRequestService.findAllBySize(1L, 0, 20)).thenReturn(itemRequestsDtoResponses);

        List<ItemRequestsDtoResponse> result = itemRequestController.findAllBySize(1L, 0, 20);

        assertEquals(itemRequestsDtoResponses, result);
    }

    @Test
    void findById() {
        long itemRequestId = 1;
        long userId = 0;
        Mockito.when(itemRequestService.findById(userId, itemRequestId)).thenReturn(itemRequestsDtoResponse);

        ItemRequestsDtoResponse result = itemRequestController.findById(userId, itemRequestId);

        assertEquals(itemRequestsDtoResponse, result);
    }
}