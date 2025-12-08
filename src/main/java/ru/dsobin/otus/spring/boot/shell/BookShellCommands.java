package ru.dsobin.otus.spring.boot.shell;

import lombok.RequiredArgsConstructor;
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

    @ShellMethod("List all books")
    public void listBooks() {
        bookDao.findAll().forEach(book ->
                io.print(String.format("%d: %s (Автор: %s, Жанр: %s)%n",
                        book.getId(), book.getTitle(),
                        book.getAuthor().getName(),
                        book.getGenre().getName()))
        );
    }

    @ShellMethod("Get book by ID")
    public void getBook(@ShellOption long id) {
        try {
            Book book = bookDao.findById(id);
            io.print(String.format("Книга: %s (Автор: %s, Жанр: %s)%n",
                    book.getTitle(), book.getAuthor().getName(), book.getGenre().getName()));
        } catch (EmptyResultDataAccessException e) {
            io.print("Книга с ID " + id + " не найдена");
        }
    }

    @ShellMethod("Create a new book")
    public void createBook(
            @ShellOption String title,
            @ShellOption long authorId,
            @ShellOption long genreId) {

        Author author = authorDao.findById(authorId);
        Genre genre = genreDao.findById(genreId);
        Book book = new Book(null, title, author, genre);
        book = bookDao.insert(book);
        io.print("Создана книга с ID: " + book.getId());
    }

    @ShellMethod("Update book")
    public void updateBook(
            @ShellOption long id,
            @ShellOption String title,
            @ShellOption long authorId,
            @ShellOption long genreId) {

        Author author = authorDao.findById(authorId);
        Genre genre = genreDao.findById(genreId);
        Book book = new Book(id, title, author, genre);
        bookDao.update(book);
        io.print("Книга обновлена");
    }

    @ShellMethod("Delete book by ID")
    public void deleteBook(@ShellOption long id) {
        bookDao.deleteById(id);
        io.print("Книга удалена");
    }
}