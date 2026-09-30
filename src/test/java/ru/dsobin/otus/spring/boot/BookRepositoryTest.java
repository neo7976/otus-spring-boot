package ru.dsobin.otus.spring.boot;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.model.Genre;
import ru.dsobin.otus.spring.boot.repository.AuthorRepository;
import ru.dsobin.otus.spring.boot.repository.BookRepository;
import ru.dsobin.otus.spring.boot.repository.GenreRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void shouldSaveAndFindBookWithAuthorAndGenre() {
        // given
        Author author = new Author();
        author.setName("Тест Автор");
        author = authorRepository.save(author);

        Genre genre = new Genre();
        genre.setName("Тест Жанр");
        genre = genreRepository.save(genre);

        Book book = new Book();
        book.setTitle("Тест Книга");
        book.setAuthor(author);
        book.setGenre(genre);

        // when
        Book saved = bookRepository.save(book);
        Book found = bookRepository.findById(saved.getId()).orElse(null);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("Тест Книга");
        assertThat(found.getAuthor().getName()).isEqualTo("Тест Автор");
        assertThat(found.getGenre().getName()).isEqualTo("Тест Жанр");
    }
}