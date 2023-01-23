package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemServiceImpl;

    @Test
    void save() {
        long userId = 0L;
        User expectedUser = new User();
        ItemDtoRequest itemDtoRequest = new ItemDtoRequest(null, "Stairs", "New Stairs",
                true, 0L, 0L);
        Item itemToSave = ItemMapper.toItem(userId, itemDtoRequest);

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        when(itemRepository.save(any(Item.class))).thenReturn(itemToSave);

        ItemDtoResponse itemDtoToResponse = itemServiceImpl.save(userId, itemDtoRequest);

        assertEquals(itemToSave.getName(), itemDtoToResponse.getName());

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void update() {
        long itemId = 0;
        long userId = 0;
        User userOwner = new User(0L, "Alex", "alex@yandex.ru");
        Item item = new Item(0L, "Stairs", "New stairs", true, 0L, null);
        ItemDtoRequest itemDtoRequest = new ItemDtoRequest(null, null, "New good Stairs",
                true, null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userOwner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDtoResponse itemUpdated = itemServiceImpl.update(userId, itemId, itemDtoRequest);

        assertEquals("Stairs", itemUpdated.getName());
        assertEquals("New good Stairs", itemUpdated.getDescription());
    }

    @Test
    void findById() {
        long itemId = 0L;
        long userId = 0L;
        Item expectedItem = new Item(
                0L, "Stairs", "New stairs", true, 0L, 0L);
        User expectedUser = new User(0L, "Maria", "maria@yandex.ru");

        when(userRepository.findById(expectedUser.getId())).thenReturn(Optional.of(expectedUser));
        when(itemRepository.findById(expectedItem.getId())).thenReturn(Optional.of(expectedItem));

        ItemDtoBooking actualItemDto = itemServiceImpl.findById(userId, itemId);

        assertEquals(expectedItem.getName(), actualItemDto.getName());

        verify(itemRepository).findById(itemId);
    }

    @Test
    void findAll() {
        long userId = 0L;
        User expectedUser = new User(0L, "Maria", "maria@yandex.ru");
        Item expectedItem = new Item(
                0L, "Stairs", "New stairs", true, expectedUser.getId(), 0L);

        Pageable sortedByIdDesc =
                PageRequest.of(0, 20, Sort.by("id").descending());
        List<Item> items = List.of(expectedItem);
        Page<Item> page = new PageImpl<>(items);

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        when(itemRepository.findAllByOwnerId(userId, sortedByIdDesc)).thenReturn(page);

        List<ItemDtoBooking> actualAllItems = itemServiceImpl.findAll(userId, 0, 20);

        assertEquals(items.size(), actualAllItems.size());

        verify(itemRepository).findAllByOwnerId(anyLong(), any());
    }

    @Test
    void search() {
        long userId = 0L;
        User expectedUser = new User(0L, "Maria", "maria@yandex.ru");
        Item expectedItem = new Item(
                0L, "Stairs", "New stairs", true, expectedUser.getId(), 0L);
        String text = "stairS";
        List<Item> items = List.of(expectedItem);
        Page<Item> page = new PageImpl<>(items);

        when(itemRepository.search(anyString(), any())).thenReturn(page);

        List<ItemDtoResponse> actualAllItems = itemServiceImpl.search(userId, text, 0, 20);

        assertEquals(items.size(), actualAllItems.size());

        verify(itemRepository).search(anyString(), any());
    }

    @Test
    void saveComment() {
        long itemId = 0L;
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item expectedItem = new Item(
                0L, "Stairs", "New stairs", true, userOwner.getId(), 0L);
        Comment expectedComment = new Comment(0L, "comment to new stairs", expectedItem, userBooker, LocalDateTime.now());
        CommentDtoCreate commentDtoCreate = new CommentDtoCreate("comment to new stairs");
        Booking expectedBooking = new Booking(0L, LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(2),
                expectedItem, userBooker, BookingState.PAST);
        List<Booking> bookings = List.of(expectedBooking);

        when(userRepository.findById(userBooker.getId())).thenReturn(Optional.of(userBooker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
        when(bookingRepository
                .getAllByItem_IdAndStartIsBeforeAndEndIsBefore(anyLong(), any(), any()))
                .thenReturn(bookings);
        when(commentRepository.save(any())).thenReturn(expectedComment);

        CommentDtoResponse comment = itemServiceImpl.save(userBooker.getId(), expectedItem.getId(), commentDtoCreate);

        assertEquals(expectedComment.getText(), comment.getText());

        verify(commentRepository).save(any());
    }
}