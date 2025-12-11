package ru.dsobin.otus.spring.boot.mapper;

import ru.dsobin.otus.spring.boot.dto.CommentDto;
import ru.dsobin.otus.spring.boot.dto.UserDto;
import ru.dsobin.otus.spring.boot.model.Comment;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        if (comment == null) return null;
        UserDto userDto = UserMapper.toDto(comment.getUser());
        return CommentDto.builder()
                .commentId(comment.getId())
                .text(comment.getText())
                .user(userDto)
                .build();
    }

    public static List<CommentDto> toDtoList(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) return Collections.emptyList();
        return comments.stream().map(CommentMapper::toDto).collect(Collectors.toList());
    }
}
