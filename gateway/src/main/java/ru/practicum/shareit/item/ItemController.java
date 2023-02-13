package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @Valid @RequestBody ItemDtoRequest itemDtoRequest) {
        log.warn("Обрабатываем запрос на создание вещи: {} от пользователя: {}", itemDtoRequest, userId);
        return itemClient.save(userId, itemDtoRequest);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long id,
                                         @RequestBody ItemDtoRequest itemDtoRequest) {
        log.info("Обрабатываем запрос на обновление вещи: {} от пользователя: {}", itemDtoRequest, userId);
        return itemClient.update(userId, id, itemDtoRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable long id) {
        log.info("Get item {}, userId={}", id, userId);
        return itemClient.findById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        log.info("Get item: userId={}, from={}, size={}", userId, from, size);
        return itemClient.findAll(userId, from, size);
    }

    // /items/search?text={text}
    @GetMapping("/search")
    public ResponseEntity<Object> searchAllByRequestText(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(value = "text") String text,
                                                         @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                         @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        log.info("Обрабатываем запрос на поиск вещи по запросу пользователя. Текст запроса: {}", text);
        if (text.isBlank()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return itemClient.searchAllByRequestText(userId, text, from, size);
    }

    // POST /items/{itemId}/comment
    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> save(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @PathVariable long id,
                                       @Valid @RequestBody CommentDtoCreate commentDtoCreate) {
        log.warn("Обрабатываем запрос на создание комметария к вещи с id = {}, комментарий = {}, от пользователя: {}",
                id, commentDtoCreate, userId);
        return itemClient.save(userId, id, commentDtoCreate);
    }

}
