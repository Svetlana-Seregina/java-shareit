package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    ItemService itemService;
    @InjectMocks
    ItemController itemController;

    @Test
    void save_whenInvoked_thenResponseWithNewItemDtoResponse() {
        long userId = 1;
        ItemDtoRequest itemDtoRequest = new ItemDtoRequest(null, "Stairs", "New Stairs",
                true, 1L, 0L);
        ItemDtoResponse itemDtoResponse = new ItemDtoResponse(0L, "Stairs", "New Stairs",
                true, 0L);
        Mockito.when(itemService.save(userId, itemDtoRequest)).thenReturn(itemDtoResponse);

        ItemDtoResponse result = itemController.save(userId, itemDtoRequest);

        assertEquals(itemDtoResponse, result);
    }


    @Test
    void update_whenInvoked_thenResponseWithUpdatedItem() {
        long userId = 1;
        long itemId = 0;
        ItemDtoRequest itemDtoRequestToUpdate = new ItemDtoRequest(null, "Super Stairs", "New Super Stairs",
                null, null, null);
        ItemDtoResponse itemDtoResponseUpdated = new ItemDtoResponse(0L, "Super Stairs", "New Super Stairs",
                true, 0L);
        Mockito.when(itemService.update(userId, itemId, itemDtoRequestToUpdate)).thenReturn(itemDtoResponseUpdated);

        ItemDtoResponse result = itemController.update(userId, itemId, itemDtoRequestToUpdate);

        assertEquals(itemDtoResponseUpdated, result);
    }

    @Test
    void findById() {
        long itemId = 0;
        long userId = 1;
        ItemDtoBooking itemDtoBooking = new ItemDtoBooking(null, "Stairs", "New Stairs", true);
        Mockito.when(itemService.findById(userId, itemId)).thenReturn(itemDtoBooking);

        ItemDtoBooking result = itemController.findById(userId, itemId);

        assertEquals(itemDtoBooking, result);
    }

    @Test
    void findAll() {
        long userId = 0;
        List<ItemDtoBooking> itemDtoBookings =
                List.of(new ItemDtoBooking(0L, "Stairs", "New Stairs", true));
        Mockito.when(itemService.findAll(userId, 0, 20)).thenReturn(itemDtoBookings);

        List<ItemDtoBooking> result = itemController.findAll(userId, 0, 20);

        assertEquals(itemDtoBookings, result);
    }

    @Test
    void searchAllByRequestText() {
        long userId = 1;
        String text = "StAiRs";
        List<ItemDtoResponse> itemDtoResponses = List.of(new ItemDtoResponse(1L, "Stairs", "New Stairs",
                true, 0L));
        Mockito.when(itemService.search(userId, text, 0, 20)).thenReturn(itemDtoResponses);

        List<ItemDtoResponse> result = itemController.searchAllByRequestText(userId, text, 0, 20);

        assertEquals(itemDtoResponses, result);
    }

    @Test
    void saveComment_whenInvoked_thenResponseWithCreatedComment() {
        long itemId = 0;
        long userId = 1;
        CommentDtoResponse commentDtoResponse = new CommentDtoResponse(1L, "Really good stairs",
                "Alex", LocalDateTime.now());
        CommentDtoCreate commentDtoCreate = new CommentDtoCreate("Really good stairs");
        Mockito.when(itemService.save(userId, itemId, commentDtoCreate)).thenReturn(commentDtoResponse);

        CommentDtoResponse result = itemController.save(userId, itemId, commentDtoCreate);

        assertEquals(commentDtoResponse, result);
    }
}