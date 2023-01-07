package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDtoRequest save(long userId, ItemDto itemDto);

    ItemDtoRequest update(long userId, long id, ItemDto itemDto);

    ItemDtoBooking findById(long userId, long id);

    List<ItemDtoBooking> findAll(long userId);

    List<ItemDtoRequest> search(long userId, String text);

    CommentDtoCreate save(long userId, long id, CommentDto commentDto);
}
