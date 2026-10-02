package ru.dsobin.otus.spring.boot.shell;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import ru.dsobin.otus.spring.boot.exception.EntityNotFoundException;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.model.Genre;
import ru.dsobin.otus.spring.boot.service.BookService;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Форматирование вывода shell-команд на настоящих messages.properties.
 */
class BookShellCommandsTest {

    private final BookService bookService = mock(BookService.class);

    private BookShellCommands commands;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        commands = new BookShellCommands(bookService, messageSource);
        LocaleContextHolder.setLocale(Locale.ENGLISH);
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void listBooks_formatsEachBookOnItsOwnLine() {
        when(bookService.findAll()).thenReturn(List.of(
                new Book(1L, "The Hobbit", new Author(1L, "Tolkien"), new Genre(2L, "Fantasy")),
                new Book(2L, "1984", new Author(3L, "Orwell"), new Genre(4L, "Dystopia"))));

        assertThat(commands.listBooks()).isEqualTo(
                "1: The Hobbit (Author: Tolkien, Genre: Fantasy)" + System.lineSeparator()
                        + "2: 1984 (Author: Orwell, Genre: Dystopia)");
    }

    @Test
    void getBook_formatsFoundBook() {
        when(bookService.findById(10L)).thenReturn(Optional.of(
                new Book(10L, "1984", new Author(3L, "Orwell"), new Genre(4L, "Dystopia"))));

        assertThat(commands.getBook(10L)).isEqualTo("Book: 1984 (Author: Orwell, Genre: Dystopia)");
    }

    @Test
    void getBook_reportsNotFound() {
        when(bookService.findById(999L)).thenReturn(Optional.empty());

        assertThat(commands.getBook(999L)).isEqualTo("Book not found with ID: 999");
    }

    @Test
    void getBook_isLocalized() {
        LocaleContextHolder.setLocale(new Locale("ru"));
        when(bookService.findById(999L)).thenReturn(Optional.empty());

        assertThat(commands.getBook(999L)).isEqualTo("Книга с ID 999 не найдена");
    }

    @Test
    void createBook_reportsMissingAuthor() {
        when(bookService.create("Title", 5L, 2L))
                .thenThrow(new EntityNotFoundException("author.not.found.with.id", 5L));

        assertThat(commands.createBook("Title", 5L, 2L)).isEqualTo("Author not found with ID: 5");
    }
}
