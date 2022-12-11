package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
        log.info("Обрабатываем запрос на создание вещи:  " + itemDto + " от пользователя: " + userId);
        return itemService.save(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long id,
                          @RequestBody ItemDto itemDto) {
        log.info("Обрабатываем запрос на обновление вещи: " + itemDto + " от пользователя: " + userId);
        return itemService.update(userId, id, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        log.info("Обрабатываем запрос на получение вещи по id = " + id + " от пользователя с id: " + userId);
        return itemService.getById(userId, id);
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Обрабатываем запрос на получение всех вещей от пользователя с id: " + userId);
        return itemService.findAll(userId);
    }

    // /items/search?text={text}
    @GetMapping("/search")
    public List<ItemDto> searchAllByRequestText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(value = "text", required = false) String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        log.info("Обрабатываем запрос на поиск вещи по запросу пользователя. Текст запроса: " + text);
        return itemService.searchAllByRequestText(userId, text);
    }
}
