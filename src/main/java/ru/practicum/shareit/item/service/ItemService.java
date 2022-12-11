package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto save(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long id, ItemDto itemDto);

    ItemDto getById(Long userId, Long id);

    List<ItemDto> findAll(Long userId);

    List<ItemDto> searchAllByRequestText(Long userId, String text);
}
