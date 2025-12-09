package ru.dsobin.otus.spring.boot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.model.Genre;
import ru.dsobin.otus.spring.boot.repository.BookRepository;

import java.util.List;
import java.util.Optional;

@Service
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

    @Transactional
    public Book create(String title, Long authorId, Long genreId) {
        Book book = new Book();
        book.setTitle(title);

        Author author = authorService.findById(authorId);
        Genre genre = genreService.findById(genreId);

        book.setAuthor(author);
        book.setGenre(genre);
        return bookRepository.save(book);
    }

    @Transactional
    public Book update(Book book, String title, Long authorId, Long genreId) {
        Author author = authorService.findById(authorId);
        Genre genre = genreService.findById(genreId);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setTitle(title);
        return bookRepository.save(book);
    }

    @Transactional
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}