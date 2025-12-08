package ru.dsobin.otus.spring.boot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.dsobin.otus.spring.boot.dao.BookDao;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.model.Genre;
import ru.dsobin.otus.spring.boot.repository.AuthorDaoJdbc;
import ru.dsobin.otus.spring.boot.repository.BookDaoJdbc;
import ru.dsobin.otus.spring.boot.repository.GenreDaoJdbc;

import java.sql.PreparedStatement;
import java.sql.Statement;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJdbcTest
@Import({BookDaoJdbc.class, AuthorDaoJdbc.class, GenreDaoJdbc.class})
class BookDaoJdbcTest {

    @Autowired
    private BookDao bookDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldInsertAndFindBook() {
        // given
        Author author = new Author(null, "Тест Автор");
        Genre genre = new Genre(null, "Тест Жанр");

        // insert via raw SQL to avoid circular deps in test
        KeyHolder authorKey = new GeneratedKeyHolder();
        new JdbcTemplate(((NamedParameterJdbcTemplate) bookDao).getJdbcTemplate().getDataSource())
                .update(conn -> {
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO authors (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, author.getName());
                    return ps;
                }, authorKey);
        author.setId(authorKey.getKey().longValue());

        KeyHolder genreKey = new GeneratedKeyHolder();
        new JdbcTemplate(((NamedParameterJdbcTemplate) bookDao).getJdbcTemplate().getDataSource())
                .update(conn -> {
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO genres (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, genre.getName());
                    return ps;
                }, genreKey);
        genre.setId(genreKey.getKey().longValue());

        Book book = new Book(null, "Тест Книга", author, genre);

        // when
        Book saved = bookDao.insert(book);
        Book found = bookDao.findById(saved.getId());

        // then
        assertThat(found.getTitle()).isEqualTo("Тест Книга");
        assertThat(found.getAuthor().getName()).isEqualTo("Тест Автор");
        assertThat(found.getGenre().getName()).isEqualTo("Тест Жанр");
    }
}