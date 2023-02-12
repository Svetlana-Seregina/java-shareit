package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
@Slf4j
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(User user, ItemRequestDtoCreate itemRequestDtoCreate) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDtoCreate.getDescription());
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public static ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest) {
        return new ItemRequestDtoResponse(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }

    public static ItemRequestsDtoResponse toItemRequestsDtoResponse(ItemRequest itemRequest) {
        return new ItemRequestsDtoResponse(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }

    public static ItemRequestsDtoResponse toItemRequestsDtoWithItem(ItemRequestsDtoResponse itemRequestsDtoResponse,
                                                                    List<Item> allItems) {
        List<ItemDtoResponse> itemDtoResponses = new ArrayList<>();
        for (Item item : allItems) {
            ItemDtoResponse itemDtoResponse = ItemMapper.toItemDtoResponse(item);
            itemDtoResponses.add(itemDtoResponse);
        }
        ItemDtoResponse[] items = itemDtoResponses.toArray(new ItemDtoResponse[0]);
        itemRequestsDtoResponse.setItems(items);
        return itemRequestsDtoResponse;
    }

    public static List<ItemRequestsDtoResponse> toItemRequestsDtoArray(List<ItemRequestsDtoResponse> itemRequestsDtoResponse) {

        ItemRequestsDtoResponse[] itemsArray = itemRequestsDtoResponse.toArray(ItemRequestsDtoResponse[]::new);
        log.info("РАЗМЕР МАССИВА: {}", itemsArray.length);

        List<ItemRequestsDtoResponse> list = Arrays.asList(itemsArray);
        log.info("РАЗМЕР СПИСКА: {}", list.size());

        return list;
    }

}
