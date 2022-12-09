package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();

    private long nextId = 1;

    public Long getNextId() {
        return nextId++;
    }

    @Override
    public Item saveNewItem(User user, Item item) {
        item.setId(getNextId());
        item.setOwner(user);
        items.put(item.getId(), item);
        log.info("Создана вещь:  " + item);
        return item;
    }

    @Override
    public Item updateItem(User user, Item item) {
        Item i = items.get(item.getId());
        if (!items.get(item.getId()).getOwner().equals(user)) {
            throw new EntityNotFoundException("У вещи другой владелец.");
        } else {
            String name = item.getName() != null ? item.getName() : i.getName();
            item.setName(name);
            String description = item.getDescription() != null ? item.getDescription() : i.getDescription();
            item.setDescription(description);
            boolean available = item.getAvailable() != null ? item.getAvailable() : i.getAvailable();
            item.setAvailable(available);
            log.info("Вещь доступна? " + available);
            item.setOwner(user);
            items.put(item.getId(), item);
            log.info("Обновлена вещь:  " + item);
            return item;
        }
    }

    @Override
    public Item getItemById(Long userId, Long id) {
        Item item = items.get(id);
        log.info("Найдена вещь по id " + id);
        return item;
    }

    @Override
    public Collection<Item> findAll(User user) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItem(Long userId, String text) {
        String t = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> {
                    log.info("В список добалена вещь пользователя: " + item);
                    return item.getName().toLowerCase().contains(t) || item.getDescription().toLowerCase().contains(t);
                })
                .collect(Collectors.toList());
    }


}
