package ru.dsobin.otus.spring.boot.dao;

import ru.dsobin.otus.spring.boot.model.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorDao {
    Optional<Author> findById(Long id);
    List<Author> findAll();
}
