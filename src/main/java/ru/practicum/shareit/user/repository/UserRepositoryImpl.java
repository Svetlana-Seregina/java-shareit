package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    private long nextId = 1;

    public Long getNextId() {
        return nextId++;
    }

    @Override
    public User saveNewUser(User user) {
        validationUserEmail(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан пользователь  " + user);
        log.info("Количество пользователей в базе = " + users.size());
        return user;
    }

    @Override
    public User updateUser(Long userId, User user) {
        User u = users.get(userId);
        validationUserEmail(user);

        String name = user.getName() != null ? user.getName() : u.getName();
        user.setName(name);
        String email = user.getEmail() != null ? user.getEmail() : u.getEmail();
        user.setEmail(email);

        log.info("Пользователь обновлен: " + user);
        users.put(userId, user);
        log.info("Количество пользователей = " + users.size());

        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Пользователь с id = " + userId + " удален.");
        users.remove(userId);
    }

    @Override
    public Collection<User> findAllUsers() {
        log.info("Количество пользователей в списке = " + users.size());
        return users.values();
    }

    @Override
    public User findUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException("Пользователь с id = " + id + " не существует.");
        }
        log.info("Найден пользователь по id: {}", users.get(id));
        return users.get(id);
    }


    private void validationUserEmail(User user) {
        Optional<User> u = users.values().stream()
                .filter(user1 -> user1.getEmail().equals(user.getEmail()))
                .findAny();
        if (u.isPresent()) {
            throw new ValidationException("Пользователь с таким email уже существует: " + user.getEmail());
        }
        log.info("EMAIL пользователя прошел проверку: в базе не дублируется.");
    }


}
