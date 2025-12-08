package ru.dsobin.otus.spring.boot.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.dsobin.otus.spring.boot.dao.AuthorDao;
import ru.dsobin.otus.spring.boot.dao.GenreDao;
import ru.dsobin.otus.spring.boot.model.Author;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AuthorDaoJdbc implements AuthorDao {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<Author> findAll() {
        return jdbc.query("SELECT id, name FROM authors",
                (rs, rowNum) -> new Author(rs.getLong("id"), rs.getString("name")));
    }

    @Override
    public Author findById(Long id) {
        return jdbc.queryForObject("SELECT id, name FROM authors WHERE id = :id",
                Map.of("id", id),
                (rs, rowNum) -> new Author(rs.getLong("id"), rs.getString("name")));
    }
}
