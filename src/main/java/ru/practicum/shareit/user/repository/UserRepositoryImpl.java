package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    private long nextId = 1;

    public Long getNextId() {
        return nextId++;
    }

    @Override
    public User saveNew(User user) {
        validationUserEmail(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан пользователь  {}", user);
        log.info("Количество пользователей в базе = {}", users.size());
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        User user1 = users.get(userId);

        if (user.getName() != null && !user.getName().isBlank()) {
            user1.setName(user.getName());
        }

        if (user.getEmail() != null && !user.getEmail().isBlank() && !user.getEmail().equals(user1.getEmail())) {
            validationUserEmail(user);
            user1.setEmail(user.getEmail());
        }

        log.info("Пользователь обновлен: {}", user);
        users.put(userId, user1);
        log.info("Количество пользователей в базе = {}", users.size());

        return user1;
    }

    @Override
    public void deleteById(Long userId) {
        log.info("Пользователь с id = {} удален.", userId);
        users.remove(userId);
    }

    @Override
    public Collection<User> findAll() {
        log.info("Количество пользователей в списке = {}", users.size());
        return List.copyOf(users.values());
    }

    @Override
    public Optional<User> findById(Long id) {
        User user = users.get(id);
        log.info("Найден пользователь по id: {}", user);
        return Optional.ofNullable(user);
    }


    private void validationUserEmail(User user) {
        String email = user.getEmail();
        users.values().stream()
                .filter(user1 -> user1.getEmail().equals(email))
                .findAny()
                .ifPresent((e) -> {
                    throw new ValidationException(String.format("Пользователь с таким email: %s уже существует.", email));
                });

        log.info("EMAIL пользователя прошел проверку: в базе не дублируется.");
    }


}
