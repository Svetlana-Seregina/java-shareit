package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;
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

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Transactional
    @Override
    public ItemDtoResponse save(long userId, ItemDtoRequest itemDtoRequest) {
        userService.findById(userId);
        log.info("Создание вещи.");
        Item item = itemRepository.save(ItemMapper.toItem(userId, itemDtoRequest));
        return ItemMapper.toItemDtoRequest(item);
    }

    @Transactional
    @Override
    public ItemDtoResponse update(long userId, long id, ItemDtoRequest itemDtoRequest) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", id)));
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещи с id = %d нет в базе.", id)));
        log.info("Обновление вещи.");

        if (!item.getOwnerId().equals(userId)) {
            throw new EntityNotFoundException(String.format("Вещи с id = %d нет у пользователя с id = %d", id, userId));
        }

        item.setName(itemDtoRequest.getName() != null && !itemDtoRequest.getName().isBlank() ?
                itemDtoRequest.getName() : item.getName());
        item.setDescription(itemDtoRequest.getDescription() != null && !itemDtoRequest.getDescription().isBlank() ?
                itemDtoRequest.getDescription() : item.getDescription());
        item.setAvailable(itemDtoRequest.getAvailable() != null ? itemDtoRequest.getAvailable() : item.getAvailable());
        log.info("Вещь обновлена: {}", item);

        return ItemMapper.toItemDtoRequest(item);
    }

    @Override
    public ItemDtoBooking findById(long userId, long id) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", id)));
        log.info("ПОЛЬЗОВАТЕЛЬ {}", user);
        log.info("Получение вещи по id = {}", id);
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещи с id = %d нет в базе.", id)));
        log.info("Найдена вещь: {}", item);

        if (item.getOwnerId().equals(userId)) {
            ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoBooking(item);

            List<Booking> lastBooking =
                    bookingRepository.getAllByItem_IdAndStartIsLessThanEqualAndEndIsLessThanEqualAndStatusIs(
                            id, LocalDateTime.now(), LocalDateTime.now(), BookingState.APPROVED);
            log.info("lastBooking найдено?: {}", lastBooking);

            List<Booking> nextBooking =
                    bookingRepository.getAllByItem_IdAndStartIsAfterAndStatusIs(
                            id, LocalDateTime.now(), BookingState.APPROVED);
            log.info("nextBooking найдено?: {}", nextBooking);

            List<Comment> comments = commentRepository.findAllByItem_Id(id);
            log.info("Список comments : {}", comments);

            if (comments.isEmpty() && lastBooking == null && nextBooking == null) {
                return itemDtoBooking;
            }
            ItemDtoBooking itemDtoWithBooking = ItemMapper.toItemDtoWithBookings(itemDtoBooking, lastBooking, nextBooking);
            ItemDtoBooking itemDtoWithComment = ItemMapper.toItemDtoBookingWithComment(itemDtoWithBooking, comments);
            log.info("ВЕЩЬ с бронированием и комментариями: {}", itemDtoWithComment);
            return itemDtoWithComment;
        }
        ItemDtoBooking itemDto = ItemMapper.toItemDtoBooking(item);
        List<Comment> comments = commentRepository.findAllByItem_Id(id);

        if (comments.isEmpty()) {
            return itemDto;
        }

        ItemDtoBooking itemDtoBookingWithComment = ItemMapper.toItemDtoBookingWithComment(itemDto, comments);
        log.info("Найдена вещь пользователя itemDto with comments = {}", itemDtoBookingWithComment);
        return itemDtoBookingWithComment;
    }

    @Override
    public List<ItemDtoBooking> findAll(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", userId)));

        List<Item> items = itemRepository.findAllByOwnerId(userId);

        Map<Long, List<Booking>> lastBookingByItemId =
                bookingRepository.getAllByItem_InAndStartIsLessThanEqualAndEndIsLessThanEqualAndStatusIs(
                                items, LocalDateTime.now(), LocalDateTime.now(), BookingState.APPROVED)
                        .stream()
                        .collect(Collectors.groupingBy(b -> b.getItem().getId(), toList()));

        Map<Long, List<Booking>> nextBookingByItemId =
                bookingRepository.getAllByItem_InAndStartAfterAndStatusIs(items, LocalDateTime.now(), BookingState.APPROVED)
                        .stream()
                        .collect(Collectors.groupingBy(b -> b.getItem().getId(), toList()));

        Map<Long, List<Comment>> comments = commentRepository.findByItem_In(items)
                .stream()
                .collect(groupingBy(c -> c.getItem().getId(), toList()));

        return items.stream()
                .map(ItemMapper::toItemDtoBooking)
                .map(itemDtoBooking -> {
                    List<Booking> lastBookings = lastBookingByItemId.get(itemDtoBooking.getId());
                    List<Booking> nextBookings = nextBookingByItemId.get(itemDtoBooking.getId());
                    if (lastBookings != null || nextBookings != null) {
                        return ItemMapper.toItemDtoWithBookings(itemDtoBooking, lastBookings, nextBookings);
                    }
                    return itemDtoBooking;
                })
                .map(itemDtoBooking -> {
                    List<Comment> allComments = comments.get(itemDtoBooking.getId());
                    if (allComments != null) {
                        return ItemMapper.toItemDtoBookingWithComment(itemDtoBooking, allComments);
                    }
                    return itemDtoBooking;
                })
                .sorted(Comparator.comparing(ItemDtoBooking::getId))
                .collect(toList());
    }

    @Override
    public List<ItemDtoResponse> search(long userId, String text) {
        log.info("Поиск вещи по запросу пользователя: {}", text);
        List<Item> itemList = itemRepository.search(text);
        log.info("Количество вещей, найденных по запросу пользователя = {}", itemList.size());
        return itemList.stream()
                .map(ItemMapper::toItemDtoRequest)
                .collect(toList());
    }

    @Transactional
    @Override
    public CommentDtoResponse save(long userId, long id, CommentDtoCreate commentDtoCreate) {
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
        Comment comment = commentRepository.save(CommentMapper.toComment(user, item, commentDtoCreate));
        log.info("Создание комментария. {}", comment);
        return CommentMapper.toCommentDtoResponse(comment);
    }

}
