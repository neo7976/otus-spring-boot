package ru.dsobin.otus.spring.boot.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.dsobin.otus.spring.boot.dao.AuthorDao;
import ru.dsobin.otus.spring.boot.dao.BookDao;
import ru.dsobin.otus.spring.boot.dao.GenreDao;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.model.Genre;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class BookDaoJdbc implements BookDao {
    private final NamedParameterJdbcTemplate jdbc;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;

    @Override
    public int count() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM books", Collections.emptyMap(), Integer.class);
    }

    @Override
    public List<Book> findAll() {
        return jdbc.query(
                "SELECT b.id, b.title, b.author_id, b.genre_id, " +
                        "a.name as author_name, g.name as genre_name " +
                        "FROM books b " +
                        "JOIN authors a ON b.author_id = a.id " +
                        "JOIN genres g ON b.genre_id = g.id",
                (rs, rowNum) -> new Book(
                        rs.getLong("id"),
                        rs.getString("title"),
                        new Author(rs.getLong("author_id"), rs.getString("author_name")),
                        new Genre(rs.getLong("genre_id"), rs.getString("genre_name"))
                )
        );
    }
    @Override
    public Book findById(Long id) {
        try {
            return jdbc.queryForObject(
                    "SELECT b.id, b.title, b.author_id, b.genre_id, " +
                            "a.name as author_name, g.name as genre_name " +
                            "FROM books b " +
                            "JOIN authors a ON b.author_id = a.id " +
                            "JOIN genres g ON b.genre_id = g.id " +
                            "WHERE b.id = :id",
                    Collections.singletonMap("id", id),
                    (rs, rowNum) -> new Book(
                            rs.getLong("id"),
                            rs.getString("title"),
                            new Author(rs.getLong("author_id"), rs.getString("author_name")),
                            new Genre(rs.getLong("genre_id"), rs.getString("genre_name"))
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        jdbc.update(
                "INSERT INTO books (title, author_id, genre_id) VALUES (:title, :authorId, :genreId)",
                new MapSqlParameterSource()
                        .addValue("title", book.getTitle())
                        .addValue("authorId", book.getAuthor().getId())
                        .addValue("genreId", book.getGenre().getId()),
                keyHolder,
                new String[]{"id"}
        );
        book.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return book;
    }

    @Override
    public Book update(Book book) {
        jdbc.update(
                "UPDATE books SET title = :title, author_id = :authorId, genre_id = :genreId WHERE id = :id",
                Map.of(
                        "id", book.getId(),
                        "title", book.getTitle(),
                        "authorId", book.getAuthor().getId(),
                        "genreId", book.getGenre().getId()
                )
        );
        return book;
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("DELETE FROM books WHERE id = :id", Map.of("id", id));
    }
}
