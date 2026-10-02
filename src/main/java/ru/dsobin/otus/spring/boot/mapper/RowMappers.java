package ru.dsobin.otus.spring.boot.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.model.Genre;

public final class RowMappers {
    public static final RowMapper<Author> AUTHOR = (rs, rowNum) ->
            new Author(rs.getLong("id"), rs.getString("name"));

    public static final RowMapper<Genre> GENRE = (rs, rowNum) ->
            new Genre(rs.getLong("id"), rs.getString("name"));


    public static final RowMapper<Book> BOOK = (rs, rowNum) ->
            new Book(
                    rs.getLong("id"),
                    rs.getString("title"),
                    new Author(rs.getLong("author_id"), rs.getString("author_name")),
                    new Genre(rs.getLong("genre_id"), rs.getString("genre_name"))
            );

    private RowMappers() {}
}
