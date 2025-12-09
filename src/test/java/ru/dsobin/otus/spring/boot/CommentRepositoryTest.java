package ru.dsobin.otus.spring.boot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.model.Comment;
import ru.dsobin.otus.spring.boot.model.Genre;
import ru.dsobin.otus.spring.boot.repository.CommentRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void shouldSaveAndFindComment() {
        // Given
        Author author = new Author();
        author.setName("Толстой");
        em.persistAndFlush(author);

        Genre genre = new Genre();
        genre.setName("Роман");
        em.persistAndFlush(genre);

        Book book = new Book();
        book.setTitle("Война и мир");
        book.setAuthor(author);
        book.setGenre(genre);
        em.persistAndFlush(book);

        Comment comment = new Comment();
        comment.setText("Отличная книга!");
        comment.setBook(book);
        em.persistAndFlush(comment);
        // When
        Comment saved = commentRepository.save(comment);
        em.flush();
        em.clear();

        List<Comment> found = commentRepository.findByBookId(book.getId());

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getText()).isEqualTo("Отличная книга!");
    }
}
