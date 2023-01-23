package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

    public static Comment toComment(User user, Item item, CommentDtoCreate commentDtoCreate) {
        Comment comment = new Comment();
        comment.setText(commentDtoCreate.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static CommentDtoResponse toCommentDtoResponse(Comment comment) {
        return new CommentDtoResponse(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

}
