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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
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
        UserDto userDto = userService.findById(userId);
        log.info("Создание вещи.");
        User user = UserMapper.toUpdateUser(userId, userDto);
        Item item = itemRepository.save(user, ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long id, ItemDto itemDto) {
        UserDto userDto = userService.findById(userId);
        log.info("Обновление вещи.");
        User user = UserMapper.toUpdateUser(userId, userDto);
        Item item = itemRepository.update(user, ItemMapper.toUpdateItem(id, itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getById(Long userId, Long id) {
        log.info("Получение вещи по id = {}", id);
        Item item = itemRepository.getById(userId, id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещи с id = %d нет в базе.", userId)));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findAll(Long userId) {
        UserDto userDto = userService.findById(userId);
        User user = UserMapper.toUpdateUser(userId, userDto);
        log.info("Получение всех вещей пользователя с id = {}", userId);
        Collection<Item> allItems = itemRepository.findAll(user);
        log.info("Всего найдено вещей пользователя = {}", allItems.size());
        log.info("Найдена вещь пользователя: {}", allItems.stream().findAny());
        return allItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAllByRequestText(Long userId, String text) {
        log.info("Поиск вещи по запросу пользователя: {}", text);
        List<Item> itemList = itemRepository.searchAllByRequestText(userId, text);
        log.info("Количество вещей, найденных по запросу пользователя = {}", itemList.size());
        return itemList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
