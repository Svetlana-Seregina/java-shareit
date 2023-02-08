package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDtoResponse save(long userId, ItemDtoRequest itemDtoRequest);

    ItemDtoResponse update(long userId, long id, ItemDtoRequest itemDtoRequest);

    ItemDtoBooking findById(long userId, long id);

    List<ItemDtoBooking> findAll(long userId, Integer from, Integer size);

    List<ItemDtoResponse> search(long userId, String text, Integer from, Integer size);

    CommentDtoResponse save(long userId, long id, CommentDtoCreate commentDtoCreate);
}
