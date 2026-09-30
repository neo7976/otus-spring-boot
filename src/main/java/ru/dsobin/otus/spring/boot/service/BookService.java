package ru.dsobin.otus.spring.boot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dsobin.otus.spring.boot.domain.BookSpecifications;
import ru.dsobin.otus.spring.boot.dto.BookDto;
import ru.dsobin.otus.spring.boot.dto.result.PageDataDto;
import ru.dsobin.otus.spring.boot.filter.BookPageInfoDto;
import ru.dsobin.otus.spring.boot.mapper.BookMapper;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.model.Genre;
import ru.dsobin.otus.spring.boot.repository.BookRepository;
import ru.dsobin.otus.spring.boot.utils.PageDataUtil;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final MessageSource messageSource;
    private static final Locale locale = LocaleContextHolder.getLocale();

    @Transactional(readOnly = true)
    public PageDataDto<BookDto> findAll(BookPageInfoDto infoDto) {
        Pageable pageable = infoDto.toPageable();
        Specification<Book> spec = BookSpecifications.withFilter(infoDto);
        Page<Book> page = bookRepository.findAll(spec, pageable);

        List<BookDto> content = page.getContent().stream()
                .map(BookMapper::toDto)
                .collect(Collectors.toList());
        return PageDataUtil.getData(page, content);
    }

    @Transactional(readOnly = true)
    public BookDto findById(Long id) {
        return bookRepository.findById(id)
                .map(BookMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage(
                        "book.not.found.with.id",
                        new Object[]{id},
                        locale)));
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
    public BookDto create(BookDto dto) {
        Author author = authorService.findById(dto.getAuthor().getAuthorId());
        Genre genre = genreService.findById(dto.getGenre().getGenreId());
        Book book = BookMapper.toEntity(dto, author, genre);

        return Optional.of(bookRepository.save(book))
                .map(BookMapper::toDto)
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage(
                        "book.not.create",
                        null,
                        locale)));
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
    public BookDto update(Long bookId, BookDto dto) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage(
                        "book.not.found.with.id",
                        new Object[]{bookId},
                        locale)));
        Author author = authorService.findById(dto.getAuthor().getAuthorId());
        Genre genre = genreService.findById(dto.getGenre().getGenreId());

        BookMapper.updateEntity(book, dto, author, genre);
        return Optional.of(bookRepository.save(book))
                .map(BookMapper::toDto)
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage(
                        "book.not.update",
                        null,
                        locale)));
    }

    @Transactional
    public boolean deleteById(Long id) {
        bookRepository.deleteById(id);
        return !bookRepository.existsById(id);
    }
}