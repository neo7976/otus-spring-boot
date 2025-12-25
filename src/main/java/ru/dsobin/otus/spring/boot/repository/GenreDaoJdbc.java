package ru.dsobin.otus.spring.boot.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.dsobin.otus.spring.boot.dao.GenreDao;
import ru.dsobin.otus.spring.boot.mapper.RowMappers;
import ru.dsobin.otus.spring.boot.model.Genre;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class GenreDaoJdbc implements GenreDao {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<Genre> findAll() {
        return jdbc.query("SELECT id, name FROM genres", RowMappers.GENRE);
    }

    @Override
    public Genre findById(Long id) {
        return jdbc.queryForObject(
                "SELECT id, name FROM genres WHERE id = :id",
                Map.of("id", id),
                RowMappers.GENRE
        );
    }
}