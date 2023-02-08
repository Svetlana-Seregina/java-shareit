package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestsDtoResponse;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDtoResponse save(long userId, ItemRequestDtoCreate itemRequestDtoCreate);

    List<ItemRequestsDtoResponse> findAll(long userId);

    List<ItemRequestsDtoResponse> findAllBySize(long userId, Integer from, Integer size);

    ItemRequestsDtoResponse findById(long userId, long id);
}
