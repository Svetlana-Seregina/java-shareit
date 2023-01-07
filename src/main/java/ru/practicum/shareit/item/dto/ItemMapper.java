package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
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

    public static Item toItem(long userId, ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(userId);
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public static ItemDtoRequest toItemDtoRequest(Item item) {
        return new ItemDtoRequest(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static ItemDtoBooking toItemDtoWithBookings(ItemDtoBooking idb, Booking lastBooking, Booking nextBooking) {
        if (lastBooking != null) {
            idb.setLastBooking(new ItemDtoBooking.Booking(lastBooking.getId(), lastBooking.getBooker().getId()));
        }
        if (nextBooking != null) {
            idb.setNextBooking(new ItemDtoBooking.Booking(nextBooking.getId(), nextBooking.getBooker().getId()));
        }
        return idb;
    }

    public static ItemDtoBooking toItemDtoBooking(Item item) {
        return new ItemDtoBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static ItemDtoBooking toItemDtoBookingWithComment(ItemDtoBooking itemDtoBooking, List<Comment> allComments) {
        List<CommentDtoCreate> comm = new ArrayList<>();
        for (Comment comment : allComments) {
            CommentDtoCreate commentDtoCreate = CommentMapper.toCommentDtoCreate(comment);
            comm.add(commentDtoCreate);
        }
        CommentDtoCreate[] comments = comm.toArray(new CommentDtoCreate[0]);
        itemDtoBooking.setComments(comments);
        return itemDtoBooking;
    }

}
