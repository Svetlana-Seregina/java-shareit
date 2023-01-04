package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId(),
                item.getRequestId()
        );
    }

    public static ItemDto toItemDtoWithComment(ItemDto itemDto, List<Comment> allComments) {
        for (Comment comment : allComments) {
            comment.setAuthorName(comment.getAuthor().getName());
        }
        Comment[] comments = allComments.toArray(new Comment[0]);
        itemDto.setComments(comments);
        return itemDto;
    }

    public static ItemDto toItemDtoWithBooking(ItemDto itemDto, Booking lastBooking, Booking nextBooking) {
        if (lastBooking.equals(new Booking()) && nextBooking.equals(new Booking())) {
            return itemDto;
        }
        itemDto.setLastBooking(lastBooking);
        User lastBookingBooker = lastBooking.getBooker();
        if (lastBookingBooker != null) {
            lastBooking.setBookerId(lastBookingBooker.getId());
        }
        itemDto.setNextBooking(nextBooking);
        User nextBookingBooker = nextBooking.getBooker();
        if (nextBookingBooker != null) {
            nextBooking.setBookerId(nextBookingBooker.getId());
        }

        return itemDto;
    }

    public static Item toItem(Long userId, ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(userId);
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public static Item toUpdateItem(ItemDto itemDtoWithId, ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDtoWithId.getId());
        item.setName(itemDto.getName() != null ? itemDto.getName() : itemDtoWithId.getName());
        item.setDescription(itemDto.getDescription() != null ? itemDto.getDescription() : itemDtoWithId.getDescription());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : itemDtoWithId.getAvailable());
        item.setOwnerId(itemDtoWithId.getOwnerId());
        item.setRequestId(itemDtoWithId.getRequestId());
        return item;
    }

}
