package ru.dsobin.otus.spring.boot.dao;

import ru.dsobin.otus.spring.boot.model.Book;

import java.util.List;

public interface BookDao {
    int count();
    List<Book> findAll();
    Book findById(Long id);
    Book insert(Book book);
    Book update(Book book);
    void deleteById(Long id);
}
