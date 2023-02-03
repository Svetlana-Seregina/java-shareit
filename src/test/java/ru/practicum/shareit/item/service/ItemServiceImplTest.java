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
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
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
    void update_whenNameIs() {
        long itemId = 0;
        long userId = 0;
        User userOwner = new User(0L, "Alex", "alex@yandex.ru");
        Item item = new Item(0L, "Stairs", "New stairs", true, 0L, null);
        ItemDtoRequest itemDtoRequest = new ItemDtoRequest(null, "Stairs, new", null,
                true, null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userOwner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDtoResponse itemUpdated = itemServiceImpl.update(userId, itemId, itemDtoRequest);

        assertEquals("Stairs, new", itemUpdated.getName());
        assertEquals("New stairs", itemUpdated.getDescription());
    }

    @Test
    void update_whenItemOwnerIsWrong_thenThrowEntityNotFoundException() {
        long itemId = 0;
        long anotherUserId = 1;
        User anotherUser = new User(1L, "Petr", "petr@yandex.ru");
        Item item = new Item(0L, "Stairs", "New stairs", true, 0L, null);
        ItemDtoRequest itemDtoRequest = new ItemDtoRequest(null, null, "New good Stairs",
                true, null, null);

        when(userRepository.findById(anotherUserId)).thenReturn(Optional.of(anotherUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class,
                () -> itemServiceImpl.update(anotherUserId, itemId, itemDtoRequest));
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
    void findById_whenBookingAndCommentExistAndFindByItemOwnerId_thenReturnWithBookingAndComment() {
        long itemId = 0L;
        long userOwnerId = 0L;
        Item expectedItem = new Item(
                0L, "Stairs", "New stairs", true, 0L, 0L);
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(0L, "Alex", "alex@yandex.ru");
        Booking nextBooking = new Booking(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(4), expectedItem, userBooker, BookingState.FUTURE);
        Booking lastBooking = new Booking(1L, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(2), expectedItem, userBooker, BookingState.PAST);
        Comment comment = new Comment(0L, "text", expectedItem, userBooker, LocalDateTime.now());

        List<Booking> lastBookings = List.of(lastBooking);
        List<Booking> nextBookings = List.of(nextBooking);
        List<Comment> comments = List.of(comment);

        when(userRepository.findById(userOwner.getId())).thenReturn(Optional.of(userOwner));
        when(itemRepository.findById(expectedItem.getId())).thenReturn(Optional.of(expectedItem));
        when(bookingRepository.getAllByItem_IdAndStartIsLessThanEqualOrEndIsLessThanEqualAndStatusIs(
                anyLong(), any(), any(), any())).thenReturn(lastBookings);
        when(bookingRepository.getAllByItem_IdAndStartIsAfterAndStatusIs(
                anyLong(), any(), any())).thenReturn(nextBookings);
        when(commentRepository.findAllByItem_Id(expectedItem.getId())).thenReturn(comments);

        ItemDtoBooking actualItemDto = itemServiceImpl.findById(userOwnerId, itemId);

        assertEquals(expectedItem.getName(), actualItemDto.getName());

        verify(itemRepository).findById(itemId);
    }

    @Test
    void findById_whenFindByNotItemOwnerAndCommentsExist_thenReturnWithComments() {
        long itemId = 0L;
        long userBookerId = 1L;
        Item expectedItem = new Item(
                0L, "Stairs", "New stairs", true, 0L, 0L);
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Comment comment = new Comment(0L, "text", expectedItem, userBooker, LocalDateTime.now());

        List<Comment> comments = List.of(comment);

        when(userRepository.findById(userBooker.getId())).thenReturn(Optional.of(userBooker));
        when(itemRepository.findById(expectedItem.getId())).thenReturn(Optional.of(expectedItem));
        when(commentRepository.findAllByItem_Id(expectedItem.getId())).thenReturn(comments);

        ItemDtoBooking actualItemDto = itemServiceImpl.findById(userBookerId, itemId);

        assertEquals(expectedItem.getName(), actualItemDto.getName());

        verify(itemRepository).findById(itemId);
    }

    @Test
    void findById_whenFindByNotItemOwnerAndCommentsNotExist_thenReturnWithoutComments() {
        long itemId = 0L;
        long userBookerId = 1L;
        Item expectedItem = new Item(
                0L, "Stairs", "New stairs", true, 0L, 0L);
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");

        when(userRepository.findById(userBooker.getId())).thenReturn(Optional.of(userBooker));
        when(itemRepository.findById(expectedItem.getId())).thenReturn(Optional.of(expectedItem));
        when(commentRepository.findAllByItem_Id(expectedItem.getId())).thenReturn(Collections.emptyList());

        ItemDtoBooking actualItemDto = itemServiceImpl.findById(userBookerId, itemId);

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
    void findAll_withoutLastBookings_thenReturnItemsWithoutLastBookings() {
        long userOwnerItemId = 0L;
        User userOwnerItem = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Petr", "petr@yandex.ru");
        Item expectedItem = new Item(
                0L, "Stairs", "New stairs", true, userOwnerItem.getId(), 0L);

        Pageable sortedByIdDesc =
                PageRequest.of(0, 20, Sort.by("id").descending());
        List<Item> items = List.of(expectedItem);
        Page<Item> page = new PageImpl<>(items);

        Booking nextBooking = new Booking(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(4), expectedItem, userBooker, BookingState.FUTURE);

        List<Booking> nextBookings = List.of(nextBooking);

        when(userRepository.findById(userOwnerItemId)).thenReturn(Optional.of(userOwnerItem));
        when(itemRepository.findAllByOwnerId(userOwnerItemId, sortedByIdDesc)).thenReturn(page);
        when(bookingRepository.getAllByItem_InAndStartIsLessThanEqualOrEndIsLessThanEqualAndStatusIs(anyList(), any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when((bookingRepository.getAllByItem_InAndStartAfterAndStatusIs(anyList(), any(), any()))).thenReturn(nextBookings);

        List<ItemDtoBooking> actualAllItems = itemServiceImpl.findAll(userOwnerItemId, 0, 20);

        assertEquals(items.size(), actualAllItems.size());

        verify(itemRepository).findAllByOwnerId(anyLong(), any());
    }

    @Test
    void findAll_withoutNextBookings_thenReturnItemsWithoutNextBookings() {
        long userOwnerItemId = 0L;
        User userOwnerItem = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Petr", "petr@yandex.ru");
        Item expectedItem = new Item(
                0L, "Stairs", "New stairs", true, userOwnerItem.getId(), 0L);

        Pageable sortedByIdDesc =
                PageRequest.of(0, 20, Sort.by("id").descending());
        List<Item> items = List.of(expectedItem);
        Page<Item> page = new PageImpl<>(items);

        Booking lastBooking = new Booking(1L, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(2), expectedItem, userBooker, BookingState.PAST);

        List<Booking> lastBookings = List.of(lastBooking);

        when(userRepository.findById(userOwnerItemId)).thenReturn(Optional.of(userOwnerItem));
        when(itemRepository.findAllByOwnerId(userOwnerItemId, sortedByIdDesc)).thenReturn(page);
        when(bookingRepository.getAllByItem_InAndStartIsLessThanEqualOrEndIsLessThanEqualAndStatusIs(anyList(), any(), any(), any()))
                .thenReturn(lastBookings);
        when((bookingRepository.getAllByItem_InAndStartAfterAndStatusIs(anyList(), any(), any()))).thenReturn(Collections.emptyList());

        List<ItemDtoBooking> actualAllItems = itemServiceImpl.findAll(userOwnerItemId, 0, 20);

        assertEquals(items.size(), actualAllItems.size());

        verify(itemRepository).findAllByOwnerId(anyLong(), any());
    }

    @Test
    void findAll_withComments_thenReturnItemsWithComments() {
        long userOwnerItemId = 0L;
        User userOwnerItem = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Petr", "petr@yandex.ru");
        Item expectedItem = new Item(
                0L, "Stairs", "New stairs", true, userOwnerItem.getId(), 0L);

        Pageable sortedByIdDesc =
                PageRequest.of(0, 20, Sort.by("id").descending());
        List<Item> items = List.of(expectedItem);
        Page<Item> page = new PageImpl<>(items);

        Booking lastBooking = new Booking(1L, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(2), expectedItem, userBooker, BookingState.PAST);
        Comment comment = new Comment(0L, "text", expectedItem, userBooker, LocalDateTime.now());

        List<Booking> lastBookings = List.of(lastBooking);
        List<Comment> comments = List.of(comment);

        when(userRepository.findById(userOwnerItemId)).thenReturn(Optional.of(userOwnerItem));
        when(itemRepository.findAllByOwnerId(userOwnerItemId, sortedByIdDesc)).thenReturn(page);
        when(bookingRepository.getAllByItem_InAndStartIsLessThanEqualOrEndIsLessThanEqualAndStatusIs(anyList(), any(), any(), any()))
                .thenReturn(lastBookings);
        when((bookingRepository.getAllByItem_InAndStartAfterAndStatusIs(anyList(), any(), any()))).thenReturn(Collections.emptyList());
        when(commentRepository.findByItem_In(items)).thenReturn(comments);

        List<ItemDtoBooking> actualAllItems = itemServiceImpl.findAll(userOwnerItemId, 0, 20);

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

    @Test
    void saveComment_whenItemDoesNotHaveBookings_thenThrowValidationException() {
        long itemId = 0L;
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        Item expectedItem = new Item(
                0L, "Stairs", "New stairs", true, userOwner.getId(), 0L);
        CommentDtoCreate commentDtoCreate = new CommentDtoCreate("comment to new stairs");

        when(userRepository.findById(userBooker.getId())).thenReturn(Optional.of(userBooker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
        when(bookingRepository
                .getAllByItem_IdAndStartIsBeforeAndEndIsBefore(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class,
                () -> itemServiceImpl.save(1L, 0L, commentDtoCreate));
    }

    @Test
    void saveComment_whenUserIsNotBooker_thenThrowValidationException() {
        long itemId = 0L;
        User userOwner = new User(0L, "Maria", "maria@yandex.ru");
        User userBooker = new User(1L, "Alex", "alex@yandex.ru");
        User userOther = new User(2L, "Petr", "petr@yandex.ru");
        Item expectedItem = new Item(
                0L, "Stairs", "New stairs", true, userOwner.getId(), 0L);
        CommentDtoCreate commentDtoCreate = new CommentDtoCreate("comment to new stairs");
        Booking expectedBooking = new Booking(0L, LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(2),
                expectedItem, userBooker, BookingState.PAST);
        List<Booking> bookings = List.of(expectedBooking);

        when(userRepository.findById(userOther.getId())).thenReturn(Optional.of(userOther));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
        when(bookingRepository
                .getAllByItem_IdAndStartIsBeforeAndEndIsBefore(anyLong(), any(), any()))
                .thenReturn(bookings);

        assertThrows(ValidationException.class,
                () -> itemServiceImpl.save(2L, 0L, commentDtoCreate));
    }
}