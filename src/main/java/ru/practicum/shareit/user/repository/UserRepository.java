package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    User saveNew(User user);

    User update(Long userId, User user);

    void deleteById(Long userId);

    Collection<User> findAll();

    Optional<User> findById(Long id);

}
