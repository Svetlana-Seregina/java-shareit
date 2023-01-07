package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                new CommentDto.Item(comment.getItem().getId(), comment.getItem().getName()),
                new CommentDto.User(comment.getAuthor().getId(), comment.getAuthor().getName()),
                comment.getCreated()
        );
    }

    public static Comment toComment(User user, Item item, CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static CommentDtoCreate toCommentDtoCreate(Comment comment) {
        return new CommentDtoCreate(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}
