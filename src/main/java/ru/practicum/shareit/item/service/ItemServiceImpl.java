package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
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
    public ItemDto saveNewItem(Long userId, ItemDto itemDto) {
        UserDto userDto = userService.findUserById(userId);
        log.info("Создание вещи.");
        User user = UserMapper.toUpdateUser(userId, userDto);
        Item item = itemRepository.saveNewItem(user, ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long id, ItemDto itemDto) {
        UserDto userDto = userService.findUserById(userId);
        log.info("Обновление вещи.");
        User user = UserMapper.toUpdateUser(userId, userDto);
        Item item = itemRepository.updateItem(user, ItemMapper.toUpdateItem(id, itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(Long userId, Long id) {
        log.info("Получение вещи по id = " + id);
        Item item = itemRepository.getItemById(userId, id);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findAll(Long userId) {
        UserDto userDto = userService.findUserById(userId);
        User user = UserMapper.toUpdateUser(userId, userDto);
        log.info("Получение всех вещей пользователя с id = " + userId);
        Collection<Item> allItems = itemRepository.findAll(user);
        log.info("Всего найдено вещей пользователя = " + allItems.size());
        log.info("Найдена вещь пользователя: " + allItems.stream().findAny());
        return allItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        log.info("Поиск вещи по запросу пользователя: " + text);
        List<Item> itemList = itemRepository.searchItem(userId, text);
        log.info("Количество вещей, найденных по запросу пользователя = " + itemList.size());
        return itemList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
