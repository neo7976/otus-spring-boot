package ru.dsobin.otus.spring.boot.service;

import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final GenreService genreService;

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
                .orElseThrow(()-> new EntityNotFoundException("Не найдена книга с ID: " + id));
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