package ru.dsobin.otus.spring.boot.dao;

import ru.dsobin.otus.spring.boot.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDao {
    Optional<Genre> findById(Long id);
    List<Genre> findAll();
}
