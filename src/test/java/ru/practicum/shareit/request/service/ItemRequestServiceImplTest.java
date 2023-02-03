package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestsDtoResponse;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestServiceImpl;

    @Test
    void save() {
        User userRequestor = new User(1L, "Alex", "alex@yandex.ru");
        ItemRequestDtoCreate itemRequestDtoCreate = new ItemRequestDtoCreate("Need 4 chairs");
        ItemRequest itemRequestExpected = new ItemRequest(0L, "Need 4 chairs", userRequestor, LocalDateTime.now());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userRequestor));
        when(itemRequestRepository.save(any())).thenReturn(itemRequestExpected);

        ItemRequestDtoResponse itemRequestActual = itemRequestServiceImpl.save(userRequestor.getId(), itemRequestDtoCreate);

        assertEquals("Need 4 chairs", itemRequestActual.getDescription());

        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void findAll() {
        User userRequestor = new User(1L, "Alex", "alex@yandex.ru");
        User userItemOwner = new User(0L, "Alex", "alex@yandex.ru");
        ItemRequest itemRequestExpected = new ItemRequest(0L, "Need 4 chairs", userRequestor, LocalDateTime.now());
        Item itemByRequest = new Item(
                0L, "Chairs", "4 chairs", true, userItemOwner.getId(), itemRequestExpected.getId());

        List<Item> items = List.of(itemByRequest);

        List<ItemRequest> itemRequests = List.of(itemRequestExpected);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userRequestor));
        when(itemRequestRepository.findAllByRequestorId(anyLong())).thenReturn(itemRequests);
        when(itemRepository.findByRequestId_In(anyCollection())).thenReturn(items);

        List<ItemRequestsDtoResponse> itemRequestActual = itemRequestServiceImpl.findAll(1);

        assertEquals(itemRequests.size(), itemRequestActual.size());

        verify(itemRequestRepository).findAllByRequestorId(anyLong());
    }

    @Test
    void findAll_whenItemRequestsAreEmpty_thenReturnEmptyList() {
        User userRequestor = new User(1L, "Alex", "alex@yandex.ru");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userRequestor));
        when(itemRequestRepository.findAllByRequestorId(anyLong())).thenReturn(Collections.emptyList());

        List<ItemRequestsDtoResponse> itemRequestActual = itemRequestServiceImpl.findAll(1);

        assertEquals(0, itemRequestActual.size());

        verify(itemRequestRepository).findAllByRequestorId(anyLong());
    }

    @Test
    void findAll_whenRequestDoesNotHaveItems_thenReturnRequestsWithoutItems() {
        User userRequestor = new User(1L, "Alex", "alex@yandex.ru");
        ItemRequest itemRequestExpected = new ItemRequest(0L, "Need 4 chairs", userRequestor, LocalDateTime.now());

        List<ItemRequest> itemRequests = List.of(itemRequestExpected);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userRequestor));
        when(itemRequestRepository.findAllByRequestorId(anyLong())).thenReturn(itemRequests);
        when(itemRepository.findByRequestId_In(anyCollection())).thenReturn(Collections.emptyList());

        List<ItemRequestsDtoResponse> itemRequestActual = itemRequestServiceImpl.findAll(1);

        assertEquals(itemRequests.size(), itemRequestActual.size());

        verify(itemRequestRepository).findAllByRequestorId(anyLong());
    }


    @Test
    void findAllBySize() {
        User userRequestor = new User(1L, "Alex", "alex@yandex.ru");
        User userItemOwner = new User(0L, "Alex", "alex@yandex.ru");
        ItemRequest itemRequestExpected = new ItemRequest(0L, "Need 4 chairs", userRequestor, LocalDateTime.now());
        Item itemByRequest = new Item(
                0L, "Chairs", "4 chairs", true, userItemOwner.getId(), itemRequestExpected.getId());

        List<Item> items = List.of(itemByRequest);

        List<ItemRequest> itemRequests = List.of(itemRequestExpected);

        Pageable sortedByCreatedDesc =
                PageRequest.of(0, 20, Sort.by("created").descending());

        Page<ItemRequest> page = new PageImpl<>(itemRequests);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userRequestor));
        when(itemRequestRepository.findAll(sortedByCreatedDesc)).thenReturn(page);
        when(itemRepository.findByRequestId_In(anyCollection())).thenReturn(items);

        List<ItemRequestsDtoResponse> itemRequestActual = itemRequestServiceImpl.findAllBySize(0, 0, 20);

        assertEquals(itemRequests.size(), itemRequestActual.size());

        verify(itemRequestRepository).findAll(sortedByCreatedDesc);
    }

    @Test
    void findAllBySize_whenRequestsWithItemsIsEmpty_thenReturnEmptyList() {
        User userRequestor = new User(0L, "Alex", "alex@yandex.ru");
        User userAnother = new User(2L, "Petr", "petr@yandex.ru");
        ItemRequest itemRequestExpected = new ItemRequest(0L, "Need 4 chairs", userRequestor, LocalDateTime.now());

        List<ItemRequest> itemRequests = List.of(itemRequestExpected);

        Pageable sortedByCreatedDesc =
                PageRequest.of(0, 20, Sort.by("created").descending());

        Page<ItemRequest> page = new PageImpl<>(itemRequests);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userAnother));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userRequestor));
        when(itemRequestRepository.findAll(sortedByCreatedDesc)).thenReturn(page);
        when(itemRepository.findByRequestId_In(anyCollection())).thenReturn(Collections.emptyList());

        List<ItemRequestsDtoResponse> itemRequestActual = itemRequestServiceImpl.findAllBySize(2L, 0, 20);

        assertEquals(1, itemRequestActual.size());

        verify(itemRequestRepository).findAll(sortedByCreatedDesc);
    }

    @Test
    void findAllBySize_whenItemRequestDoesNotHaveAnyItem_thenReturnListWithoutItems() {
        User userRequestor = new User(0L, "Alex", "alex@yandex.ru");
        User userAnother = new User(2L, "Petr", "petr@yandex.ru");
        ItemRequest itemRequestExpected = new ItemRequest(0L, "Need 4 chairs", userRequestor, LocalDateTime.now());
        Item itemByRequest = new Item(
                0L, "Chairs", "4 chairs", true, 1L, itemRequestExpected.getId());

        List<Item> items = List.of(itemByRequest);

        List<ItemRequest> itemRequests = List.of(itemRequestExpected);

        Pageable sortedByCreatedDesc =
                PageRequest.of(0, 20, Sort.by("created").descending());

        Page<ItemRequest> page = new PageImpl<>(itemRequests);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userAnother));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userRequestor));
        when(itemRequestRepository.findAll(sortedByCreatedDesc)).thenReturn(page);
        when(itemRepository.findByRequestId_In(anyCollection())).thenReturn(items);

        List<ItemRequestsDtoResponse> itemRequestActual = itemRequestServiceImpl.findAllBySize(2L, 0, 20);

        assertEquals(1, itemRequestActual.size());

        verify(itemRequestRepository).findAll(sortedByCreatedDesc);
    }


    @Test
    void findAllBySize_whenFindByItemRequestsOwner_thenReturnEmptyList() {
        User userRequestor = new User(0L, "Alex", "alex@yandex.ru");
        User userAnother = new User(2L, "Petr", "petr@yandex.ru");
        ItemRequest itemRequestExpected = new ItemRequest(0L, "Need 4 chairs", userRequestor, LocalDateTime.now());

        List<ItemRequest> itemRequests = List.of(itemRequestExpected);

        Pageable sortedByCreatedDesc =
                PageRequest.of(0, 20, Sort.by("created").descending());

        Page<ItemRequest> page = new PageImpl<>(itemRequests);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userAnother));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userRequestor));
        when(itemRequestRepository.findAll(sortedByCreatedDesc)).thenReturn(page);

        List<ItemRequestsDtoResponse> itemRequestActual = itemRequestServiceImpl.findAllBySize(0L, 0, 20);

        assertEquals(0, itemRequestActual.size());

        verify(itemRequestRepository).findAll(sortedByCreatedDesc);
    }

    @Test
    void findById() {
        User userRequestor = new User(1L, "Alex", "alex@yandex.ru");
        User userItemOwner = new User(0L, "Alex", "alex@yandex.ru");
        ItemRequest itemRequestExpected = new ItemRequest(0L, "Need 4 chairs", userRequestor, LocalDateTime.now());
        Item itemByRequest = new Item(
                4L, "Chairs", "4 chairs", true, userItemOwner.getId(), itemRequestExpected.getId());

        List<Item> items = List.of(itemByRequest);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userRequestor));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequestExpected));
        when(itemRepository.getItemsByRequestId(anyLong())).thenReturn(items);

        ItemRequestsDtoResponse itemRequestActual = itemRequestServiceImpl.findById(1L, 0L);

        assertEquals(itemRequestExpected.getDescription(), itemRequestActual.getDescription());

        verify(itemRequestRepository).findById(anyLong());
    }
}