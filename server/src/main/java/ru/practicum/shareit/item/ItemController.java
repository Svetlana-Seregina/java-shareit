package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDtoResponse save(@RequestHeader("X-Sharer-User-Id") long userId,
                                @RequestBody ItemDtoRequest itemDtoRequest) {
        log.warn("Обрабатываем запрос на создание вещи: {} от пользователя: {}", itemDtoRequest, userId);
        return itemService.save(userId, itemDtoRequest);
    }

    @PatchMapping("/{id}")
    public ItemDtoResponse update(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable long id,
                                  @RequestBody ItemDtoRequest itemDtoRequest) {
        log.info("Обрабатываем запрос на обновление вещи: {} от пользователя: {}", itemDtoRequest, userId);
        return itemService.update(userId, id, itemDtoRequest);
    }

    @GetMapping("/{id}")
    public ItemDtoBooking findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long id) {
        log.warn("Обрабатываем запрос на получение вещи по id = {} от пользователя: {}", id, userId);
        return itemService.findById(userId, id);
    }

    @GetMapping
    public List<ItemDtoBooking> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                        @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        log.info("Обрабатываем запрос на получение всех вещей от пользователя с id: {}", userId);
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
        log.info("Обрабатываем запрос на поиск вещи по запросу пользователя. Текст запроса: {}", text);
        return itemService.search(userId, text, from, size);
    }

    // POST /items/{itemId}/comment
    @PostMapping("/{id}/comment")
    public CommentDtoResponse save(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long id,
                                   @RequestBody CommentDtoCreate commentDtoCreate) {
        log.warn("Обрабатываем запрос на создание комметария к вещи с id = {}, комментарий = {}, от пользователя: {}",
                id, commentDtoCreate, userId);
        return itemService.save(userId, id, commentDtoCreate);
    }

}
