package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
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
    public Item save(User user, Item item) {
        item.setId(getNextId());
        item.setOwner(user);
        items.put(item.getId(), item);
        log.info("Создана вещь:  {}", item);
        return item;
    }

    @Override
    public Item update(User user, Item item) {
        Long itemId = item.getId();
        String itemName = item.getName();
        String itemDescription = item.getDescription();
        Boolean itemAvailable = item.getAvailable();
        Item item1 = items.get(itemId);

        if (!items.get(itemId).getOwner().equals(user)) {
            throw new EntityNotFoundException("У вещи другой владелец.");
        } else {
            String name = itemName != null && !itemName.isBlank() ? itemName : item1.getName();
            item.setName(name);
            String description = itemDescription != null && !itemDescription.isBlank() ? itemDescription : item1.getDescription();
            item.setDescription(description);
            boolean available = itemAvailable != null ? itemAvailable : item1.getAvailable();
            item.setAvailable(available);
            log.info("Вещь доступна? {}", available);
            item.setOwner(user);
            items.put(itemId, item);
            log.info("Обновлена вещь: {} ", item);
            return item;
        }
    }

    @Override
    public Optional<Item> getById(Long userId, Long id) {
        Optional<Item> item = Optional.of(items.get(id));
        log.info("Найдена вещь по id {}", id);
        return item;
    }

    @Override
    public Collection<Item> findAll(User user) {
        return List.copyOf(items.values().stream()
                .filter(item -> item.getOwner().equals(user))
                .collect(Collectors.toList()));
    }

    @Override
    public List<Item> searchAllByRequestText(Long userId, String text) {
        String lowerCaseText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> {
                    log.info("В список добалена вещь пользователя: {}", item);
                    return item.getName().toLowerCase().contains(lowerCaseText) || item.getDescription().toLowerCase().contains(lowerCaseText);
                })
                .collect(Collectors.toList());
    }

}
