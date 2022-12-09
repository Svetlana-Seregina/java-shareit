package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto saveNewItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long id, ItemDto itemDto);

    ItemDto getItemById(Long userId, Long id);

    List<ItemDto> findAll(Long userId);

    List<ItemDto> searchItem(Long userId, String text);
}
