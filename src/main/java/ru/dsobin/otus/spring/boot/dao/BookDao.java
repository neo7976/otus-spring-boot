package ru.dsobin.otus.spring.boot.dao;

import ru.dsobin.otus.spring.boot.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao {
    int count();
    List<Book> findAll();
    Optional<Book> findById(Long id);
    Book insert(Book book);
    Book update(Book book);
    void deleteById(Long id);
}
