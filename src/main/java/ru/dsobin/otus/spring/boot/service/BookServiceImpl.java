package ru.dsobin.otus.spring.boot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dsobin.otus.spring.boot.dao.AuthorDao;
import ru.dsobin.otus.spring.boot.dao.BookDao;
import ru.dsobin.otus.spring.boot.dao.GenreDao;
import ru.dsobin.otus.spring.boot.exception.EntityNotFoundException;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.model.Genre;

import java.util.List;
import java.util.Optional;

/**
 * Транзакции нужны только там, где несколько обращений к БД должны выполниться атомарно
 * (create/update: чтение автора, жанра и запись книги). Чтение одним запросом и удаление
 * одним оператором атомарны сами по себе.
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookDao bookDao;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;

    @Override
    public List<Book> findAll() {
        return bookDao.findAll();
    }

    @Override
    public Optional<Book> findById(long id) {
        return bookDao.findById(id);
    }

    @Override
    @Transactional
    public Book create(String title, long authorId, long genreId) {
        Book book = new Book(null, title, findAuthor(authorId), findGenre(genreId));
        return bookDao.insert(book);
    }

    @Override
    @Transactional
    public Book update(long id, String title, long authorId, long genreId) {
        if (bookDao.findById(id).isEmpty()) {
            throw new EntityNotFoundException("book.not.found.with.id", id);
        }
        Book book = new Book(id, title, findAuthor(authorId), findGenre(genreId));
        return bookDao.update(book);
    }

    @Override
    public void deleteById(long id) {
        bookDao.deleteById(id);
    }

    private Author findAuthor(long authorId) {
        return authorDao.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("author.not.found.with.id", authorId));
    }

    private Genre findGenre(long genreId) {
        return genreDao.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("genre.not.found.with.id", genreId));
    }
}
