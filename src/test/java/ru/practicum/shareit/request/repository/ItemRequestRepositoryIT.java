package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRequestRepositoryIT {

    @Autowired
    ItemRequestRepository itemRequestRepository;

    /*@Test
    void findAllByRequestorId() {
        List<ItemRequest> allByRequestorId = itemRequestRepository.findAllByRequestorId(0L);
        assertTrue(allByRequestorId.isEmpty());
    }*/
}