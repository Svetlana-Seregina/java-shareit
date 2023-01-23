package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDtoResponse save(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDtoRequest itemDtoRequest) {
        log.warn("Обрабатываем запрос на создание вещи: {} от пользователя: {}", itemDtoRequest, userId);
        return itemService.save(userId, itemDtoRequest);
    }

    @PatchMapping("/{id}")
    public ItemDtoResponse update(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable long id,
                                  @RequestBody ItemDtoRequest itemDtoRequest) {
        log.warn("Обрабатываем запрос на обновление вещи: {} от пользователя: {}", itemDtoRequest, userId);
        return itemService.update(userId, id, itemDtoRequest);
    }

    @GetMapping("/{id}")
    public ItemDtoBooking findById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id) {
        log.warn("Обрабатываем запрос на получение вещи по id = {} от пользователя: {}", id, userId);
        return itemService.findById(userId, id);
    }

    @GetMapping
    public List<ItemDtoBooking> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                        @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        log.info("Обрабатываем запрос на получение всех вещей от пользователя с id: {}", userId);
        if (from != null && size != null) {
            if (from < 0 || size <= 0) {
                throw new ValidationException(String.format("Значения from не может быть отрицательным (from =%d) и " +
                        "size равняться или быть меньше нуля (size =%d)", from, size));
            }
        }
        return itemService.findAll(userId, from, size);
    }

    // /items/search?text={text}
    @GetMapping("/search")
    public List<ItemDtoResponse> searchAllByRequestText(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(value = "text") String text,
                                                        @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                        @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        if (from != null && size != null) {
            if (from < 0 || size <= 0) {
                throw new ValidationException(String.format("Значения from не может быть отрицательным (from =%d) и " +
                        "size равняться или быть меньше нуля (size =%d)", from, size));
            }
        }
        log.info("Обрабатываем запрос на поиск вещи по запросу пользователя. Текст запроса: {}", text);
        return itemService.search(userId, text, from, size);
    }

    // POST /items/{itemId}/comment
    @PostMapping("/{id}/comment")
    public CommentDtoResponse save(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long id,
                                   @Valid @RequestBody CommentDtoCreate commentDtoCreate) {
        log.warn("Обрабатываем запрос на создание комметария к вещи с id = {}, комментарий = {}, от пользователя: {}",
                id, commentDtoCreate, userId);
        return itemService.save(userId, id, commentDtoCreate);
    }

}
