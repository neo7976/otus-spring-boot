package ru.dsobin.otus.spring.boot.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.dsobin.otus.spring.boot.dto.BookDto;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.service.BookService;
import ru.dsobin.otus.spring.boot.service.ConsoleIOService;

@ShellComponent
@RequiredArgsConstructor
public class BookShellCommands {

    private final BookService bookService;
    private final ConsoleIOService io;
    private final MessageSource messageSource;

//    @ShellMethod(key = {"list-books", "l-b"}, value = "List all books with authors and genres")
//    public void listBooks() {
//        var locale = LocaleContextHolder.getLocale();
//        String authorTranslate = messageSource.getMessage("book.Author.translate", null, locale);
//        String genreTranslate = messageSource.getMessage("book.Genre.translate", null, locale);
//        bookService.findAll().forEach(book ->
//                io.print(String.format("%d: %s (%s: %s, %s: %s)%n",
//                        book.getId(),
//                        book.getTitle(),
//                        authorTranslate,
//                        book.getAuthor().getName(),
//                        genreTranslate,
//                        book.getGenre().getName()))
//        );
//    }

    @ShellMethod(key = {"get-book", "g-b"}, value = "Get book by ID")
    public void getBook(@ShellOption long id) {
        var locale = LocaleContextHolder.getLocale();
        try {
            BookDto book = bookService.findById(id);
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
        } catch (Exception e) {
            io.print(messageSource.getMessage("book.not.found.with.id", new Object[]{id}, locale));
        }
    }

    @ShellMethod(key = {"create-book", "c-b"}, value = "Create a new book")
    public void createBook(
            @ShellOption String title,
            @ShellOption long authorId,
            @ShellOption long genreId) {

        var locale = LocaleContextHolder.getLocale();
        Book book = bookService.create(title, authorId, genreId);
        io.print(messageSource.getMessage("book.create.with.id", new Object[]{book.getId()}, locale));
    }

//    @ShellMethod(key = {"update-book", "upd-b"}, value = "Update book")
//    public void updateBook(
//            @ShellOption long id,
//            @ShellOption String title,
//            @ShellOption long authorId,
//            @ShellOption long genreId) {
//
//        var locale = LocaleContextHolder.getLocale();
//        bookService.findById(id).ifPresentOrElse(book ->
//                {
//                    bookService.update(book, title, authorId, genreId);
//                    io.print(messageSource.getMessage("book.update", null, locale));
//                },
//                () -> io.print(messageSource.getMessage("book.not.found.with.id", new Object[]{id}, locale)));
//
//    }

    @ShellMethod(key = {"delete-book", "d-b"}, value = "Delete book by ID")
    public void deleteBook(@ShellOption long id) {
        bookService.deleteById(id);
        var locale = LocaleContextHolder.getLocale();
        io.print(messageSource.getMessage("book.delete", null, locale));
    }
}