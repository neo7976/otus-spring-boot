package ru.dsobin.otus.spring.boot.dao;

import ru.dsobin.otus.spring.boot.model.Author;

import java.util.List;

public interface AuthorDao {
    Author findById(Long id);
    List<Author> findAll();
}
