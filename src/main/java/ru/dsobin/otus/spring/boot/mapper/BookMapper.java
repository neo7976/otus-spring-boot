package ru.dsobin.otus.spring.boot.mapper;

import ru.dsobin.otus.spring.boot.dto.AuthorDto;
import ru.dsobin.otus.spring.boot.dto.BookDto;
import ru.dsobin.otus.spring.boot.dto.CommentDto;
import ru.dsobin.otus.spring.boot.dto.GenreDto;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.model.Genre;

import java.util.List;
import java.util.Optional;

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

    public static Book toEntity(BookDto dto, Author author, Genre genre) {
        Book book = new Book();
        book.setTitle(Optional.ofNullable(dto.getTitle()).orElse("Пусто название"));

        book.setAuthor(author);
        book.setGenre(genre);
        return book;
    }

    public static Book updateEntity(Book book, BookDto dto, Author author, Genre genre) {
        book.setTitle(dto.getTitle() == null ? book.getTitle() : dto.getTitle());

        book.setAuthor(author);
        book.setGenre(genre);
        return book;
    }
}
