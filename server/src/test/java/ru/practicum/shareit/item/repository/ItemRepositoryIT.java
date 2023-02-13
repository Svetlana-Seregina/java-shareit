package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositiry.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryIT {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    private User userItemOwner;


    @BeforeEach
    void init() {
        userItemOwner = userRepository.save(new User(null, "Petr", "petr_iv@yandex.ru"));
        itemRepository.save(
                new Item(null, "Лестница", "Складная лестница", true, userItemOwner.getId(), null));
    }

    @AfterEach
    void deleteAll() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void search() {
        Pageable sortedByIdtDesc =
                PageRequest.of(0, 20, Sort.by("id").ascending());
        Page<Item> actualItemsBySearch = itemRepository.search("ЛесТниЦА", sortedByIdtDesc);

        assertEquals(1, actualItemsBySearch.getTotalElements());
    }

    @Test
    void findAllByOwnerId() {
        userRepository.findById(userItemOwner.getId());

        itemRepository.save(Item.builder()
                .name("Лестница2")
                .description("Складная лестница2")
                .available(true)
                .ownerId(userItemOwner.getId())
                .build());

        Pageable sortedByStartDesc =
                PageRequest.of(0, 20, Sort.by("id").ascending());
        Page<Item> allByOwner1 = itemRepository.findAllByOwnerId(userItemOwner.getId(), sortedByStartDesc);

        assertEquals(2, allByOwner1.getTotalElements());
    }

    @Test
    void findByRequestId_In() {
        User userRequestor = userRepository.save(new User(null, "Alex", "alex@yandex.ru"));
        ItemRequest itemRequest = itemRequestRepository.save(
                new ItemRequest(null, "Нужна складная лестница", userRequestor, LocalDateTime.now()));
        itemRepository.save(
                new Item(null, "Лестница", "Складная лестница", true, userItemOwner.getId(), itemRequest.getId()));
        List<Long> listIds = new ArrayList<>();
        listIds.add(itemRequest.getId());
        List<Item> byRequestIdIn = itemRepository.findByRequestId_In(listIds);

        assertEquals(1, byRequestIdIn.size());
    }

    @Test
    void getItemsByRequestId() {
        User userRequestor = userRepository.save(new User(null, "Alex", "alex@yandex.ru"));
        ItemRequest itemRequest = itemRequestRepository.save(
                new ItemRequest(null, "Нужна складная лестница", userRequestor, LocalDateTime.now()));
        itemRepository.save(
                new Item(null, "Лестница", "Складная лестница", true, userItemOwner.getId(), itemRequest.getId()));

        List<Item> items = itemRepository.getItemsByRequestId(itemRequest.getId());
        assertEquals(1, items.size());
    }
}