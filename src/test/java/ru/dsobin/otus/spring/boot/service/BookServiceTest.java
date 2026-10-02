package ru.dsobin.otus.spring.boot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.dsobin.otus.spring.boot.dao.AuthorDao;
import ru.dsobin.otus.spring.boot.dao.BookDao;
import ru.dsobin.otus.spring.boot.dao.GenreDao;
import ru.dsobin.otus.spring.boot.exception.EntityNotFoundException;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.model.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    private static final Author AUTHOR = new Author(1L, "Tolkien");
    private static final Genre GENRE = new Genre(2L, "Fantasy");

    @Mock
    private BookDao bookDao;
    @Mock
    private AuthorDao authorDao;
    @Mock
    private GenreDao genreDao;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void findAll_returnsBooksFromDao() {
        Book book = new Book(1L, "The Hobbit", AUTHOR, GENRE);
        when(bookDao.findAll()).thenReturn(List.of(book));

        assertThat(bookService.findAll()).containsExactly(book);
    }

    @Test
    void findById_returnsBookWhenFound() {
        Book book = new Book(10L, "1984", AUTHOR, GENRE);
        when(bookDao.findById(10L)).thenReturn(Optional.of(book));

        assertThat(bookService.findById(10L)).contains(book);
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        when(bookDao.findById(999L)).thenReturn(Optional.empty());

        assertThat(bookService.findById(999L)).isEmpty();
    }

    @Test
    void create_insertsBookWithAuthorAndGenre() {
        when(authorDao.findById(1L)).thenReturn(Optional.of(AUTHOR));
        when(genreDao.findById(2L)).thenReturn(Optional.of(GENRE));
        when(bookDao.insert(any(Book.class))).thenAnswer(inv -> {
            Book book = inv.getArgument(0);
            book.setId(100L);
            return book;
        });

        Book created = bookService.create("New Book", 1L, 2L);

        assertThat(created.getId()).isEqualTo(100L);
        assertThat(created.getTitle()).isEqualTo("New Book");
        assertThat(created.getAuthor()).isEqualTo(AUTHOR);
        assertThat(created.getGenre()).isEqualTo(GENRE);
    }

    @Test
    void create_throwsWhenAuthorNotFound() {
        when(authorDao.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.create("New Book", 5L, 2L))
                .isInstanceOf(EntityNotFoundException.class)
                .extracting("messageCode").isEqualTo("author.not.found.with.id");
        verify(bookDao, never()).insert(any());
    }

    @Test
    void update_updatesExistingBook() {
        Book existing = new Book(50L, "Old Title", AUTHOR, GENRE);
        when(bookDao.findById(50L)).thenReturn(Optional.of(existing));
        when(authorDao.findById(1L)).thenReturn(Optional.of(AUTHOR));
        when(genreDao.findById(2L)).thenReturn(Optional.of(GENRE));
        when(bookDao.update(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        Book updated = bookService.update(50L, "Updated Title", 1L, 2L);

        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        verify(bookDao).update(argThat(b -> b.getId().equals(50L) && b.getTitle().equals("Updated Title")));
    }

    @Test
    void update_throwsWhenBookNotFound() {
        when(bookDao.findById(50L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.update(50L, "Title", 1L, 2L))
                .isInstanceOf(EntityNotFoundException.class)
                .extracting("messageCode").isEqualTo("book.not.found.with.id");
        verify(bookDao, never()).update(any());
    }

    @Test
    void deleteById_delegatesToDao() {
        bookService.deleteById(77L);

        verify(bookDao).deleteById(77L);
    }
}
