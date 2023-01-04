package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
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
    public ItemDto save(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.warn("Обрабатываем запрос на создание вещи: {} от пользователя: {}", itemDto, userId);
        return itemService.save(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long id,
                          @RequestBody ItemDto itemDto) {
        log.warn("Обрабатываем запрос на обновление вещи: {} от пользователя: {}", itemDto, userId);
        return itemService.update(userId, id, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        log.warn("Обрабатываем запрос на получение вещи по id = {} от пользователя: {}", id, userId);
        return itemService.findById(userId, id);
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Обрабатываем запрос на получение всех вещей от пользователя с id: {}", userId);
        return itemService.findAll(userId);
    }

    // /items/search?text={text}
    @GetMapping("/search")
    public List<ItemDto> searchAllByRequestText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(value = "text") String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        log.info("Обрабатываем запрос на поиск вещи по запросу пользователя. Текст запроса: {}", text);
        return itemService.search(userId, text);
    }

    // POST /items/{itemId}/comment
    @PostMapping("/{id}/comment")
    public CommentDto save(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long id,
                           @Valid @RequestBody CommentDto commentDto) {
        log.warn("Обрабатываем запрос на создание комметария к вещи с id = {}, комментарий = {}, от пользователя: {}",
                id, commentDto, userId);
        return itemService.save(userId, id, commentDto);
    }


}
