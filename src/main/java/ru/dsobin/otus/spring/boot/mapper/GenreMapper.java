package ru.dsobin.otus.spring.boot.mapper;

import ru.dsobin.otus.spring.boot.dto.GenreDto;
import ru.dsobin.otus.spring.boot.model.Genre;

public class GenreMapper {
    public static GenreDto toDto(Genre genre) {
        if (genre == null) return null;
        return GenreDto.builder()
                .genreId(genre.getId())
                .name(genre.getName())
                .build();
    }
}
