package ru.dsobin.otus.spring.boot.mapper;

import ru.dsobin.otus.spring.boot.dto.AuthorDto;
import ru.dsobin.otus.spring.boot.model.Author;

public class AuthorMapper {
    public static AuthorDto toDto(Author author) {
        if (author == null) return null;
        return AuthorDto.builder()
                .authorId(author.getId())
                .name(author.getName())
                .build();

    }
}
