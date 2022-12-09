package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

public interface ItemRepository {

    Item saveNewItem(User user, Item item);

    Item updateItem(User user, Item item);

    Item getItemById(Long userId, Long id);

    Collection<Item> findAll(User user);

    List<Item> searchItem(Long userId, String text);

}
