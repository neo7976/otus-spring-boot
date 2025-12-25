package ru.dsobin.otus.spring.boot.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.dsobin.otus.spring.boot.service.BookService;

@ShellComponent
@RequiredArgsConstructor
public class BookShellCommands {
    private final BookService bookService;

    @ShellMethod("List all books")
    public void listBooks() {
        bookService.listBooks();
    }

    @ShellMethod("Get book by ID")
    public void getBook(@ShellOption long id) {
        bookService.getBook(id);
    }

    @ShellMethod("Create a new book")
    public void createBook(
            @ShellOption String title,
            @ShellOption long authorId,
            @ShellOption long genreId) {

        bookService.createBook(title, authorId, genreId);
    }

    @ShellMethod("Update book")
    public void updateBook(
            @ShellOption long id,
            @ShellOption String title,
            @ShellOption long authorId,
            @ShellOption long genreId) {

        bookService.updateBook(id, title, authorId, genreId);
    }

    @ShellMethod("Delete book by ID")
    public void deleteBook(@ShellOption long id) {
        bookService.deleteBook(id);
    }
}