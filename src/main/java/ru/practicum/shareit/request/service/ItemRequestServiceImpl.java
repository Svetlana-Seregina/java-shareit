package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDtoResponse save(long userId, ItemRequestDtoCreate itemRequestDtoCreate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", userId)));
        log.info("Создание запроса на вещь.");
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.toItemRequest(user, itemRequestDtoCreate));
        return ItemRequestMapper.toItemRequestDtoResponse(itemRequest);
    }

    @Override
    public List<ItemRequestsDtoResponse> findAll(long userId) { // список своих запросов
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", userId)));
        log.info("Поиск запросов на вещь.");

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(userId);
        log.info("РАЗМЕР СПИСКА: itemRequests.size =  " + itemRequests.size());

        if (itemRequests.size() == 0) {
            return Collections.emptyList();
        }

        List<Long> itemRequestsId = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestsId.add(itemRequest.getId());
        }
        log.info("СПИСОК: itemRequestsId.size =  " + itemRequestsId.size());

        List<Item> items = itemRepository.findByRequestId_In(itemRequestsId);
        log.info("СПИСОК: items.size =  " + items.size());

        if (items.size() == 0) {
            List<ItemRequestsDtoResponse> itemRequestDto = itemRequests.stream()
                    .map(ItemRequestMapper::toItemRequestsDtoResponse)
                    .collect(toList());
            return ItemRequestMapper.toItemRequestsDtoArray(itemRequestDto);
        }

        Map<Long, List<Item>> itemsByRequest = items
                .stream()
                .collect(Collectors.groupingBy(Item::getRequestId, toList()));

        List<ItemRequestsDtoResponse> requestsDtoResponses = itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestsDtoResponse)
                .map(itemRequestsDtoResponse -> {
                    List<Item> allItems = itemsByRequest.get(itemRequestsDtoResponse.getId());
                    if (allItems != null) {
                        return ItemRequestMapper.toItemRequestsDtoWithItem(itemRequestsDtoResponse, allItems);
                    }
                    return itemRequestsDtoResponse;
                })
                .sorted(Comparator.comparing(ItemRequestsDtoResponse::getCreated))
                .collect(toList());
        log.info("СПИСОК: requestsDtoResponses.size =  " + requestsDtoResponses.size());
        return ItemRequestMapper.toItemRequestsDtoArray(requestsDtoResponses);
    }

    @Override
    public List<ItemRequestsDtoResponse> findAllBySize(long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", userId)));
        log.info("Поиск запросов на вещь, постранично.");
        Pageable sortedByCreatedDesc =
                PageRequest.of(from, size, Sort.by("created").descending());
        Page<ItemRequest> itemRequests = itemRequestRepository.findAll(sortedByCreatedDesc);

        List<ItemRequest> itemRequestWithoutOwner = itemRequests.stream()
                .filter(itemRequest -> itemRequest.getRequestor().getId() != userId)
                .collect(toList());
        log.info("СПИСОК ЗАПРОСОВ itemRequestWithoutOwner: " + itemRequestWithoutOwner.size());

        if (itemRequestWithoutOwner.size() == 0) {
            return Collections.emptyList();
        }

        List<Long> itemRequestsId = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestWithoutOwner) {
            itemRequestsId.add(itemRequest.getId());
        }
        log.info("СПИСОК: itemRequestsId.size =  " + itemRequestsId.size());

        List<Item> items = itemRepository.findByRequestId_In(itemRequestsId);
        log.info("СПИСОК: items.size =  " + items.size());

        if (items.size() == 0) {
            List<ItemRequestsDtoResponse> itemRequestDto = itemRequestWithoutOwner.stream()
                    .map(ItemRequestMapper::toItemRequestsDtoResponse)
                    .collect(toList());
            return ItemRequestMapper.toItemRequestsDtoArray(itemRequestDto);
        }

        Map<Long, List<Item>> itemsByRequest = items
                .stream()
                .collect(Collectors.groupingBy(Item::getRequestId, toList()));

        List<ItemRequestsDtoResponse> requestsDtoResponses = itemRequestWithoutOwner.stream()
                .map(ItemRequestMapper::toItemRequestsDtoResponse)
                .map(itemRequestsDtoResponse -> {
                    List<Item> allItems = itemsByRequest.get(itemRequestsDtoResponse.getId());
                    if (allItems != null) {
                        return ItemRequestMapper.toItemRequestsDtoWithItem(itemRequestsDtoResponse, allItems);
                    }
                    return itemRequestsDtoResponse;
                })
                .sorted(Comparator.comparing(ItemRequestsDtoResponse::getCreated))
                .collect(toList());
        log.info("СПИСОК: requestsDtoResponses.size =  " + requestsDtoResponses.size());
        return ItemRequestMapper.toItemRequestsDtoArray(requestsDtoResponses);
    }

    @Override
    public ItemRequestsDtoResponse findById(long userId, long id) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", userId)));
        log.info("Поиск запроса на вещь по id.");
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Запроса с id = %d нет в базе.", id)));
        List<Item> items = itemRepository.getItemsByRequestId(id);
        return ItemRequestMapper.toItemRequestsDtoWithItem(ItemRequestMapper.toItemRequestsDtoResponse(itemRequest), items);
    }



}
