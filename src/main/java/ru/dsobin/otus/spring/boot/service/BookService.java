package ru.dsobin.otus.spring.boot.service;

import ru.dsobin.otus.spring.boot.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {

    List<Book> findAll();

    Optional<Book> findById(long id);

    Book create(String title, long authorId, long genreId);

    Book update(long id, String title, long authorId, long genreId);

    void deleteById(long id);
}
