package ru.dsobin.otus.spring.boot.mapper;

import ru.dsobin.otus.spring.boot.dto.AuthorDto;
import ru.dsobin.otus.spring.boot.dto.BookDto;
import ru.dsobin.otus.spring.boot.dto.CommentDto;
import ru.dsobin.otus.spring.boot.dto.GenreDto;
import ru.dsobin.otus.spring.boot.model.Book;

import java.util.List;

public class BookMapper {
    public static BookDto toDto(Book book) {
        if (book == null) return null;

        AuthorDto authorDto = AuthorMapper.toDto(book.getAuthor());
        GenreDto genreDto = GenreMapper.toDto(book.getGenre());
        List<CommentDto> commentList = CommentMapper.toDtoList(book.getComments());

        return BookDto.builder()
                .bookId(book.getId())
                .title(book.getTitle())
                .author(authorDto)
                .genre(genreDto)
                .comments(commentList)
                .build();
    }
}
