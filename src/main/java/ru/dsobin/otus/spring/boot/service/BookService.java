package ru.dsobin.otus.spring.boot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dsobin.otus.spring.boot.dao.AuthorDao;
import ru.dsobin.otus.spring.boot.dao.BookDao;
import ru.dsobin.otus.spring.boot.dao.GenreDao;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.model.Genre;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookDao bookDao;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;
    private final ConsoleIOService io;
    private final MessageSource messageSource;


    @Transactional
    public void listBooks() {
        var locale = LocaleContextHolder.getLocale();
        String authorTranslate = messageSource.getMessage("book.Author.translate", null, locale);
        String genreTranslate = messageSource.getMessage("book.Genre.translate", null, locale);
        bookDao.findAll().forEach(book ->
                io.print(String.format("%d: %s (%s: %s, %s: %s)%n",
                        book.getId(),
                        book.getTitle(),
                        authorTranslate,
                        book.getAuthor().getName(),
                        genreTranslate,
                        book.getGenre().getName()))
        );
    }

    @Transactional
    public void getBook(long id) {
        var locale = LocaleContextHolder.getLocale();
        try {
            Book book = bookDao.findById(id);
            String authorTranslate = messageSource.getMessage("book.Author.translate", null, locale);
            String genreTranslate = messageSource.getMessage("book.Genre.translate", null, locale);
            String bookTranslate = messageSource.getMessage("book.Book.translate", null, locale);
            io.print(String.format("%s: %s (%s: %s, %s: %s)%n",
                    bookTranslate,
                    book.getTitle(),
                    authorTranslate,
                    book.getAuthor().getName(),
                    genreTranslate,
                    book.getGenre().getName()));
        } catch (EmptyResultDataAccessException e) {
            io.print(messageSource.getMessage("book.not.found.with.id", new Object[]{id}, locale));
        }
    }


    @Transactional
    public void createBook(String title, long authorId, long genreId) {

        var locale = LocaleContextHolder.getLocale();
        Author author = authorDao.findById(authorId);
        Genre genre = genreDao.findById(genreId);
        Book book = new Book(null, title, author, genre);
        book = bookDao.insert(book);
        io.print(messageSource.getMessage("book.create.with.id", new Object[]{book.getId()}, locale));
    }

    @Transactional
    public void updateBook(long id, String title, long authorId, long genreId) {

        var locale = LocaleContextHolder.getLocale();
        Author author = authorDao.findById(authorId);
        Genre genre = genreDao.findById(genreId);
        Book book = new Book(id, title, author, genre);
        bookDao.update(book);
        io.print(messageSource.getMessage("book.update", null, locale));
    }

    @Transactional
    public void deleteBook(long id) {
        bookDao.deleteById(id);
        var locale = LocaleContextHolder.getLocale();
        io.print(messageSource.getMessage("book.delete", null, locale));
    }
}
