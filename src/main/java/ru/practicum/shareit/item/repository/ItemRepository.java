package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(User user, Item item);

    Item update(User user, Item item);

    Optional<Item> getById(Long userId, Long id);

    Collection<Item> findAll(User user);

    List<Item> searchAllByRequestText(Long userId, String text);

}
