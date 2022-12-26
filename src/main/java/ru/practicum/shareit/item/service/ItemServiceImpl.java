package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto save(Long userId, ItemDto itemDto) {
        userService.findById(userId);
        log.info("Создание вещи.");
        Item item = itemRepository.save(ItemMapper.toItem(userId, itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long id, ItemDto itemDto) {
        userService.findById(userId);
        log.info("Обновление вещи.");
        ItemDto itemDtoWithId = findById(userId, id);
        if (!itemDtoWithId.getOwnerId().equals(userId)) {
            throw new EntityNotFoundException(String.format("Вещи с id = %d нет у пользователя с id = %d", id, userId));
        }
        Item item = itemRepository.save(ItemMapper.toUpdateItem(itemDtoWithId, itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto findById(Long userId, Long id) {
        userService.findById(userId);
        log.info("Получение вещи по id = {}", id);
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещи с id = %d нет в базе.", id)));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        log.info("Найдена вещь пользователя item = {}", itemDto);
        return itemDto;
    }

    @Override
    public List<ItemDto> findAll(Long userId) {
        userService.findById(userId);
        log.info("Получение всех вещей пользователя с id = {}", userId);
        List<Item> allItems = itemRepository.findAll();

        List<ItemDto> allItemsDto = allItems.stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Всего найдено вещей пользователя = {}", allItemsDto.size());
        log.info("Найдена вещь пользователя: {}", allItemsDto.stream().findAny());

        return allItemsDto;
    }

    @Override
    public List<ItemDto> search(Long userId, String text) {
        log.info("Поиск вещи по запросу пользователя: {}", text);
        List<Item> itemList = itemRepository.search(text);
        log.info("Количество вещей, найденных по запросу пользователя = {}", itemList.size());
        return itemList.stream()
                .filter(item -> item.getAvailable().equals(true))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
