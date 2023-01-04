package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto save(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long id, ItemDto itemDto);

    ItemDto findById(Long userId, Long id);

    List<ItemDto> findAll(Long userId);

    List<ItemDto> search(Long userId, String text);

    CommentDto save(Long userId, Long id, CommentDto commentDto);
}
