package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
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
        log.info("Вещь обновлена: {}", item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto findById(Long userId, Long id) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", id)));
        log.info("ПОЛЬЗОВАТЕЛЬ {}", user);
        log.info("Получение вещи по id = {}", id);
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещи с id = %d нет в базе.", id)));
        log.info("Найдена вещь: {}", item);

        if (item.getOwnerId().equals(userId)) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            log.info("вещь ItemDto : {}", itemDto);
            ItemDto itemDtoWithBooking = getLastAndNextBooking(itemDto.getId(), itemDto);
            log.info("вещь itemDtoWithBooking : {}", itemDtoWithBooking);
            List<Comment> comments = commentRepository.findAllByItem_Id(id);
            log.info("Список comments : {}", comments);
            if (comments.isEmpty()) {
                return itemDtoWithBooking;
            }

            ItemDto itemDtoWithComment = ItemMapper.toItemDtoWithComment(itemDtoWithBooking, comments);
            log.info("ВЕЩЬ с бронированием и комментариями: {}", itemDtoWithComment);
            return itemDtoWithBooking;
        }
        ItemDto itemDto = ItemMapper.toItemDto(item);
        List<Comment> comments = commentRepository.findAllByItem_Id(id);
        log.info("Найдена ВЕЩЬ пользователя itemDto = {}", itemDto);

        if (comments.isEmpty()) {
            return itemDto;
        }

        ItemDto i = ItemMapper.toItemDtoWithComment(itemDto, comments);
        log.info("Найдена вещь пользователя itemDto with comments = {}", i);
        return i;
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

        List<ItemDto> allItemsDtoWithBooking = new ArrayList<>();
        List<Booking> allI = bookingRepository.getAllByItem_OwnerId(userId);
        log.info("ВСЕ ВЕЩИ ПОЛЬЗОВАТЕЛЯ: {}", allI);
        for (ItemDto itemDto : allItemsDto) {
            ItemDto itemDtoWithBooking = getLastAndNextBooking(itemDto.getId(), itemDto);
            log.info("вещь itemDtoWithBooking : {}", itemDtoWithBooking);
            allItemsDtoWithBooking.add(itemDtoWithBooking);
        }
        allItemsDtoWithBooking.sort(Comparator.comparing(ItemDto::getId));

        return allItemsDtoWithBooking;
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

    @Override
    public CommentDto save(Long userId, Long id, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", id)));
        log.info("ПОЛЬЗОВАТЕЛЬ {}", user);
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещи с id = %d нет в базе.", id)));
        log.info("ВЕЩЬ {}", item);
        List<Booking> bookings = bookingRepository
                .getAllByItem_IdAndStartIsBeforeAndEndIsBefore(id, LocalDateTime.now(), LocalDateTime.now());
        log.info("БРОНИРОВАНИЯ {}", bookings);
        if (bookings.size() == 0) {
            throw new ValidationException("У вещи нет бронирований");
        }
        Comment comment = commentRepository.save(CommentMapper.toComment(user, item, commentDto));
        log.info("Создание комментария. {}", comment);
        return CommentMapper.toCommentDto(comment);
    }

    private ItemDto getLastAndNextBooking(Long id, ItemDto itemDto) {
        Booking lastBooking =
                bookingRepository.getAllByItem_IdAndStartIsBefore(id, LocalDateTime.now()).orElse(new Booking());
        log.info("lastBooking найдено?: {}", lastBooking);

        Booking nextBooking =
                bookingRepository.getAllByItem_IdAndStartIsAfter(id, LocalDateTime.now()).orElse(new Booking());
        log.info("nextBooking найдено?: {}", nextBooking);

        ItemDto itemDtoWithBooking = ItemMapper.toItemDtoWithBooking(itemDto, lastBooking, nextBooking);
        log.info("Найдены бронирования: {}", itemDtoWithBooking);
        return itemDtoWithBooking;
    }

}
