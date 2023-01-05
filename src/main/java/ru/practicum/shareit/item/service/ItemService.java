package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto save(long userId, ItemDto itemDto);

    ItemDto update(long userId, long id, ItemDto itemDto);

    ItemDto findById(long userId, long id);

    List<ItemDto> findAll(long userId);

    List<ItemDto> search(long userId, String text);

    CommentDto save(long userId, long id, CommentDto commentDto);
}
