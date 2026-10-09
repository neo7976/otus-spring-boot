package ru.dsobin.otus.spring.boot.dao;

import ru.dsobin.otus.spring.boot.model.Genre;

import java.util.List;

public interface GenreDao {
    Genre findById(Long id);
    List<Genre> findAll();
}
