package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @Valid @RequestBody ItemRequestDtoCreate itemRequestDtoCreate) {
        log.info("Обрабатываем запрос на создание запроса на вещь = {}, от пользователя: {}",
                itemRequestDtoCreate, userId);
        return itemRequestClient.save(userId, itemRequestDtoCreate);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обрабатываем запрос на получение всех запросов на вещь от пользователя: {}",
                userId);
        return itemRequestClient.findAll(userId);
    }

    //GET /requests/all?from=0&size=0
    @GetMapping("/all")
    public ResponseEntity<Object> findAllBySize(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                @Positive @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        log.info("Обрабатываем запрос на получение запросов на вещь, от пользователя: {}, " +
                "отображение может быть постраничное. Индекс первого элемента, начиная с 0 = {}," +
                " количество элементов для отображения на странице = {}", userId, from, size);
        return itemRequestClient.findAllBySize(userId, from, size);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable long id) {
        log.info("Обрабатываем запрос на получение запроса на вещь с id = {} от пользователя: {}", id,
                userId);
        return itemRequestClient.findById(userId, id);
    }
}
