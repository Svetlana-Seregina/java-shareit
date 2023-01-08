package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemMapper {

    public static ItemDtoRequest toItemDto(Item item) {
        return new ItemDtoRequest(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId(),
                item.getRequestId()
        );
    }


    public static Item toItem(long userId, ItemDtoRequest itemDtoRequest) {
        Item item = new Item();
        item.setName(itemDtoRequest.getName());
        item.setDescription(itemDtoRequest.getDescription());
        item.setAvailable(itemDtoRequest.getAvailable());
        item.setOwnerId(userId);
        item.setRequestId(itemDtoRequest.getRequestId());
        return item;
    }

    public static ItemDtoResponse toItemDtoRequest(Item item) {
        return new ItemDtoResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static ItemDtoBooking toItemDtoWithBookings(ItemDtoBooking idb, List<Booking> lastBooking, List<Booking> nextBooking) {
        if (lastBooking != null) {
            for (Booking lb : lastBooking) {
                idb.setLastBooking(new ItemDtoBooking.Booking(lb.getId(), lb.getBooker().getId()));
            }
        }
        if (nextBooking != null) {
            for (Booking nb : nextBooking) {
                idb.setNextBooking(new ItemDtoBooking.Booking(nb.getId(), nb.getBooker().getId()));
            }
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
        List<CommentDtoResponse> comm = new ArrayList<>();
        for (Comment comment : allComments) {
            CommentDtoResponse commentDtoCreate = CommentMapper.toCommentDtoResponse(comment);
            comm.add(commentDtoCreate);
        }
        CommentDtoResponse[] comments = comm.toArray(new CommentDtoResponse[0]);
        itemDtoBooking.setComments(comments);
        return itemDtoBooking;
    }

}
