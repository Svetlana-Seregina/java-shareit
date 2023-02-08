package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @Transactional
    @Override
    public ItemDtoResponse save(long userId, ItemDtoRequest itemDtoRequest) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", userId)));
        log.info("Создание вещи.");
        Item item = itemRepository.save(ItemMapper.toItem(userId, itemDtoRequest));
        return ItemMapper.toItemDtoResponse(item);
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

        return ItemMapper.toItemDtoResponse(item);
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

            List<Booking> lastBookings =
                    bookingRepository.getAllByItem_IdAndStartIsLessThanEqualOrEndIsLessThanEqualAndStatusIs(
                            id, LocalDateTime.now(), LocalDateTime.now(), BookingState.APPROVED);
            Booking lastBooking = lastBookings.stream().max(Comparator.comparing(Booking::getStart)).orElse(null);

            log.info("lastBooking найдено?: {}", lastBookings);

            List<Booking> nextBookings =
                    bookingRepository.getAllByItem_IdAndStartIsAfterAndStatusIs(
                            id, LocalDateTime.now(), BookingState.APPROVED);
            Booking nextBooking = nextBookings.stream().min(Comparator.comparing(Booking::getStart)).orElse(null);

            log.info("nextBooking найдено?: {}", nextBookings);

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
    public List<ItemDtoBooking> findAll(long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", userId)));
        log.info("ПОЛЬЗОВАТЕЛЬ = {}", user.getName());
        Pageable sortedByIdDesc =
                PageRequest.of(0, size, Sort.by("id").descending());

        List<Item> items = itemRepository.findAllByOwnerId(userId, sortedByIdDesc)
                .stream()
                .collect(toList());
        log.info("ВЕЩИ ПОЛЬЗОВАТЕЛЯ = {}", items.size());

        Map<Long, List<Booking>> lastBookingsByItemId =
                bookingRepository.getAllByItem_InAndStartIsLessThanEqualOrEndIsLessThanEqualAndStatusIs(
                                items, LocalDateTime.now(), LocalDateTime.now(), BookingState.APPROVED)
                        .stream()
                        .collect(Collectors.groupingBy(b -> b.getItem().getId(), toList()));
        log.info("lastBookingsByItemId = {}", lastBookingsByItemId.size());

        Map<Long, List<Booking>> nextBookingsByItemId =
                bookingRepository.getAllByItem_InAndStartAfterAndStatusIs(
                                items, LocalDateTime.now(), BookingState.APPROVED)
                        .stream()
                        .collect(Collectors.groupingBy(b -> b.getItem().getId(), toList()));
        log.info("nextBookingsByItemId = {}", nextBookingsByItemId.size());

        Map<Long, List<Comment>> comments = commentRepository.findByItem_In(items)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId(), toList()));
        log.info("comments = {}", comments.size());

        return items.stream()
                .map(ItemMapper::toItemDtoBooking)
                .map(itemDtoBooking -> {

                    if (lastBookingsByItemId.isEmpty() && nextBookingsByItemId.isEmpty()) {
                        return itemDtoBooking;
                    }
                    List<Booking> lb = lastBookingsByItemId.get(itemDtoBooking.getId());
                    Booking lastBooking = null;
                    if (lb != null) {
                        lastBooking = lastBookingsByItemId.get(itemDtoBooking.getId())
                                .stream()
                                .max(Comparator.comparing(Booking::getStart))
                                .orElse(null);
                    }
                    List<Booking> nb = nextBookingsByItemId.get(itemDtoBooking.getId());
                    Booking nextBooking = null;
                    if (nb != null) {
                        nextBooking = nextBookingsByItemId.get(itemDtoBooking.getId())
                                .stream()
                                .min(Comparator.comparing(Booking::getStart))
                                .orElse(null);
                    }
                    return ItemMapper.toItemDtoWithBookings(itemDtoBooking, lastBooking, nextBooking);
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
    public List<ItemDtoResponse> search(long userId, String text, Integer from, Integer size) {
        log.info("Поиск вещи по запросу пользователя: {}", text);
        Pageable sortedByIdtDesc =
                PageRequest.of(0, size, Sort.by("id").ascending());
        List<Item> itemList = itemRepository.search(text, sortedByIdtDesc)
                .stream()
                .collect(toList());
        log.info("Количество вещей, найденных по запросу пользователя = {}", itemList.size());
        return itemList.stream()
                .map(ItemMapper::toItemDtoResponse)
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
            throw new ValidationException("У вещи нет завершенных бронирований");
        }
        for (Booking booking : bookings) {
            if (!booking.getBooker().getId().equals(userId)) {
                throw new ValidationException("Пользователь не бронировавший вещь не может оставлять комментарий.");
            }
        }
        Comment comment = commentRepository.save(CommentMapper.toComment(user, item, commentDtoCreate));
        log.info("Создание комментария. {}", comment);
        return CommentMapper.toCommentDtoResponse(comment);
    }

}
