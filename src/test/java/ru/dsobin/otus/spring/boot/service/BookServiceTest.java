package ru.dsobin.otus.spring.boot.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.dsobin.otus.spring.boot.dao.AuthorDao;
import ru.dsobin.otus.spring.boot.dao.BookDao;
import ru.dsobin.otus.spring.boot.dao.GenreDao;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.model.Genre;

import java.util.Locale;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookDao bookDao;
    @Mock
    private AuthorDao authorDao;
    @Mock
    private GenreDao genreDao;
    @Mock
    private ConsoleIOService io;
    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private BookService bookService;

    private final Locale testLocale = Locale.ENGLISH;

    @BeforeEach
    void setUp() {
        // Устанавливаем локаль для всех тестов (имитируем LocaleContextHolder)
        LocaleContextHolder.setLocale(testLocale);
    }

    @Test
    void listBooks_shouldPrintAllBooks() {
        // given
        Author author = new Author(1L, "Tolkien");
        Genre genre = new Genre(2L, "Fantasy");
        Book book = new Book(1L, "The Hobbit", author, genre);
        when(bookDao.findAll()).thenReturn(java.util.List.of(book));
        when(messageSource.getMessage("book.Author.translate", null, testLocale)).thenReturn("Author");
        when(messageSource.getMessage("book.Genre.translate", null, testLocale)).thenReturn("Genre");

        // when
        bookService.listBooks();

        // then
        String expected = "1: The Hobbit (Author: Tolkien, Genre: Fantasy)" + System.lineSeparator();
        verify(io).print(expected);

        verify(bookDao).findAll();
    }

    @Test
    void getBook_shouldPrintBookWhenFound() {
        // given
        Author author = new Author(1L, "Orwell");
        Genre genre = new Genre(2L, "Dystopia");
        Book book = new Book(10L, "1984", author, genre);
        when(bookDao.findById(10L)).thenReturn(book);
        when(messageSource.getMessage("book.Book.translate", null, testLocale)).thenReturn("Book");
        when(messageSource.getMessage("book.Author.translate", null, testLocale)).thenReturn("Author");
        when(messageSource.getMessage("book.Genre.translate", null, testLocale)).thenReturn("Genre");

        // when
        bookService.getBook(10L);

        // then
        String expected = "Book: 1984 (Author: Orwell, Genre: Dystopia)" + System.lineSeparator();
        verify(io).print(expected);
    }

    @Test
    void getBook_shouldHandleNotFound() {
        // given
        when(bookDao.findById(999L)).thenThrow(new EmptyResultDataAccessException(1));
        when(messageSource.getMessage("book.not.found.with.id", new Object[]{999L}, testLocale))
                .thenReturn("Book with ID 999 not found");

        // when
        bookService.getBook(999L);

        // then
        verify(io).print("Book with ID 999 not found");
        verify(bookDao).findById(999L);
    }

    @Test
    void createBook_shouldInsertAndPrintSuccessMessage() {
        // given
        Author author = new Author(1L, "Author");
        Genre genre = new Genre(2L, "Genre");
        Book insertedBook = new Book(100L, "New Book", author, genre);
        when(authorDao.findById(1L)).thenReturn(author);
        when(genreDao.findById(2L)).thenReturn(genre);
        when(bookDao.insert(any(Book.class))).thenReturn(insertedBook);
        when(messageSource.getMessage("book.create.with.id", new Object[]{100L}, testLocale))
                .thenReturn("Book created with ID 100");

        // when
        bookService.createBook("New Book", 1L, 2L);

        // then
        verify(bookDao).insert(argThat(b ->
                b.getTitle().equals("New Book") &&
                        b.getAuthor().getId().equals(1L) &&
                        b.getGenre().getId().equals(2L)
        ));
        verify(io).print("Book created with ID 100");
    }

    @Test
    void updateBook_shouldUpdateAndPrintSuccessMessage() {
        // given
        Author author = new Author(3L, "Updated Author");
        Genre genre = new Genre(4L, "Updated Genre");
        when(authorDao.findById(3L)).thenReturn(author);
        when(genreDao.findById(4L)).thenReturn(genre);
        when(messageSource.getMessage("book.update", null, testLocale))
                .thenReturn("Book updated");

        // when
        bookService.updateBook(50L, "Updated Title", 3L, 4L);

        // then
        verify(bookDao).update(argThat(b ->
                b.getId().equals(50L) &&
                        b.getTitle().equals("Updated Title") &&
                        b.getAuthor().getId().equals(3L) &&
                        b.getGenre().getId().equals(4L)
        ));
        verify(io).print("Book updated");
    }

    @Test
    void deleteBook_shouldDeleteAndPrintSuccessMessage() {
        // given
        when(messageSource.getMessage("book.delete", null, testLocale))
                .thenReturn("Book deleted");

        // when
        bookService.deleteBook(77L);

        // then
        verify(bookDao).deleteById(77L);
        verify(io).print("Book deleted");
    }
}