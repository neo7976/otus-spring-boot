package ru.dsobin.otus.spring.boot.data;

import ru.dsobin.otus.spring.boot.dto.AuthorDto;
import ru.dsobin.otus.spring.boot.dto.BookDto;
import ru.dsobin.otus.spring.boot.dto.GenreDto;

public class BookData {
    public static BookDto testDto() {
        return BookDto.builder()
                .title("Test book")
                .genre(new GenreDto(1L))
                .author(new AuthorDto(2L))
                .build();
    }

    public static BookDto testUpdDto() {
        return BookDto.builder()
                .title("Test book - Update")
                .genre(new GenreDto(2L))
                .author(new AuthorDto(1L))
                .build();
    }
}
