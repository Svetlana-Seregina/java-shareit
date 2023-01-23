package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestsDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;


    @PostMapping
    public ItemRequestDtoResponse save(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @Valid @RequestBody ItemRequestDtoCreate itemRequestDtoCreate) {
        log.info("Обрабатываем запрос на создание запроса на вещь = {}, от пользователя: {}",
                itemRequestDtoCreate, userId);
        return itemRequestService.save(userId, itemRequestDtoCreate);
    }

    @GetMapping
    public List<ItemRequestsDtoResponse> findAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Обрабатываем запрос на получение всех запросов на вещь от пользователя: {}",
                userId);
        return itemRequestService.findAll(userId);
    }

    //GET /requests/all?from=0&size=0
    @GetMapping("/all")
    public List<ItemRequestsDtoResponse> findAllBySize(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                       @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        log.info("Обрабатываем запрос на получение запросов на вещь, от пользователя: {}, " +
                        "отображение может быть постраничное. Индекс первого элемента, начиная с 0 = {}," +
                        " количество элементов для отображения на странице = {}", userId, from, size);
        if(from != null && size != null) {
            if(from < 0 || size <= 0) {
                throw new ValidationException(String.format("Значения from не может быть отрицательным (from =%d) и " +
                        "size равняться или быть меньше нуля (size =%d)", from, size));
            }
        }
        return itemRequestService.findAllBySize(userId, from, size);
    }

    @GetMapping("{id}")
    public ItemRequestsDtoResponse findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @PathVariable long id) {
        log.info("Обрабатываем запрос на получение запроса на вещь с id = {} от пользователя: {}", id,
                userId);
        return itemRequestService.findById(userId, id);
    }

}
