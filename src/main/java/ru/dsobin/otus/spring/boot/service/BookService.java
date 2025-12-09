package ru.dsobin.otus.spring.boot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.model.Genre;
import ru.dsobin.otus.spring.boot.repository.AuthorRepository;
import ru.dsobin.otus.spring.boot.repository.BookRepository;
import ru.dsobin.otus.spring.boot.repository.GenreRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final GenreService genreService;

    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public Book create(Book book) {
        return bookRepository.save(book);
    }

    public Book create(String title, Long authorId, Long genreId) {
        Book book = new Book();
        book.setTitle(title);

        Author author = authorService.findById(authorId);
        Genre genre = genreService.findById(genreId);

        book.setAuthor(author);
        book.setGenre(genre);
        return create(book);
    }

    public Book update(Book book) {
        return bookRepository.save(book);
    }

    public Book update(Book book, Long authorId, Long genreId) {
        Author author = authorService.findById(authorId);
        Genre genre = genreService.findById(genreId);
        book.setAuthor(author);
        book.setGenre(genre);
        return bookRepository.save(book);
    }

    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
