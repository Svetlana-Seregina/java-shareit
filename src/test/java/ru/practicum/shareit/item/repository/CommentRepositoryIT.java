package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryIT {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CommentRepository commentRepository;

    private Item item;

    @BeforeEach
    void init() {
        User userItemOwner = userRepository.save(new User(null, "Pavel", "pavel@yandex.ru"));
        User userItemBooker = userRepository.save(new User(null, "Alex", "alex@yandex.ru"));
        item = itemRepository.save(new Item(null, "Stairs", "New stairs", true, userItemOwner.getId(), null));
        commentRepository.save(new Comment(null, "good stairs", item, userItemBooker, LocalDateTime.now()));
    }

    @AfterEach
    void clear() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByItem_Id() {
        List<Comment> comments = commentRepository.findAllByItem_Id(item.getId());
        assertEquals(1, comments.size());
        assertEquals("good stairs", comments.get(0).getText());
    }

    @Test
    void findByItem_In() {
        List<Item> items = List.of(item);
        List<Comment> comments = commentRepository.findByItem_In(items);
        assertEquals(1, comments.size());
        assertEquals(item.getName(), comments.get(0).getItem().getName());
    }
}