package ru.dsobin.otus.spring.boot.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.dsobin.otus.spring.boot.exception.EntityNotFoundException;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.service.BookService;

import java.util.stream.Collectors;

/**
 * Слой представления: вызывает сервис и форматирует результат.
 * Spring Shell сам печатает строку, которую вернул метод.
 */
@ShellComponent
@RequiredArgsConstructor
public class BookShellCommands {

    private final BookService bookService;

    private final MessageSource messageSource;

    @ShellMethod("List all books")
    public String listBooks() {
        return bookService.findAll().stream()
                .map(book -> book.getId() + ": " + formatBook(book))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod("Get book by ID")
    public String getBook(@ShellOption long id) {
        return bookService.findById(id)
                .map(book -> message("book.Book.translate") + ": " + formatBook(book))
                .orElseGet(() -> message("book.not.found.with.id", id));
    }

    @ShellMethod("Create a new book")
    public String createBook(
            @ShellOption String title,
            @ShellOption long authorId,
            @ShellOption long genreId) {
        try {
            Book book = bookService.create(title, authorId, genreId);
            return message("book.create.with.id", book.getId());
        } catch (EntityNotFoundException e) {
            return message(e.getMessageCode(), e.getArgs());
        }
    }

    @ShellMethod("Update book")
    public String updateBook(
            @ShellOption long id,
            @ShellOption String title,
            @ShellOption long authorId,
            @ShellOption long genreId) {
        try {
            bookService.update(id, title, authorId, genreId);
            return message("book.update");
        } catch (EntityNotFoundException e) {
            return message(e.getMessageCode(), e.getArgs());
        }
    }

    @ShellMethod("Delete book by ID")
    public String deleteBook(@ShellOption long id) {
        bookService.deleteById(id);
        return message("book.delete");
    }

    private String formatBook(Book book) {
        return String.format("%s (%s: %s, %s: %s)",
                book.getTitle(),
                message("book.Author.translate"), book.getAuthor().getName(),
                message("book.Genre.translate"), book.getGenre().getName());
    }

    private String message(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
