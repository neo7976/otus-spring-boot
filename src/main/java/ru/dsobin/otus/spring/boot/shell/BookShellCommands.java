package ru.dsobin.otus.spring.boot.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.dsobin.otus.spring.boot.dao.AuthorDao;
import ru.dsobin.otus.spring.boot.dao.BookDao;
import ru.dsobin.otus.spring.boot.dao.GenreDao;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.model.Genre;
import ru.dsobin.otus.spring.boot.service.ConsoleIOService;

@ShellComponent
@RequiredArgsConstructor
public class BookShellCommands {

    private final BookDao bookDao;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;
    private final ConsoleIOService io;
    private final MessageSource messageSource;

    @ShellMethod("List all books")
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

    @ShellMethod("Get book by ID")
    public void getBook(@ShellOption long id) {
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

    @ShellMethod("Create a new book")
    public void createBook(
            @ShellOption String title,
            @ShellOption long authorId,
            @ShellOption long genreId) {

        var locale = LocaleContextHolder.getLocale();
        Author author = authorDao.findById(authorId);
        Genre genre = genreDao.findById(genreId);
        Book book = new Book(null, title, author, genre);
        book = bookDao.insert(book);
        io.print(messageSource.getMessage("book.create.with.id", new Object[]{book.getId()}, locale));
    }

    @ShellMethod("Update book")
    public void updateBook(
            @ShellOption long id,
            @ShellOption String title,
            @ShellOption long authorId,
            @ShellOption long genreId) {

        var locale = LocaleContextHolder.getLocale();
        Author author = authorDao.findById(authorId);
        Genre genre = genreDao.findById(genreId);
        Book book = new Book(id, title, author, genre);
        bookDao.update(book);
        io.print(messageSource.getMessage("book.update", null, locale));
    }

    @ShellMethod("Delete book by ID")
    public void deleteBook(@ShellOption long id) {
        bookDao.deleteById(id);
        var locale = LocaleContextHolder.getLocale();
        io.print(messageSource.getMessage("book.delete", null, locale));
    }
}