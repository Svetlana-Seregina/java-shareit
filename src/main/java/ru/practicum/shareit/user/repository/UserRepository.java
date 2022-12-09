package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {

    User saveNewUser(User user);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);

    Collection<User> findAllUsers();

    User findUserById(Long id);

}
