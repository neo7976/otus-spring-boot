package ru.dsobin.otus.spring.boot.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.dsobin.otus.spring.boot.repository.AuthorRepository;
import ru.dsobin.otus.spring.boot.repository.BookRepository;
import ru.dsobin.otus.spring.boot.repository.GenreRepository;

/**
 * Собственный health-индикатор: библиотека считается рабочей, если в каталоге есть книги.
 * В /actuator/health отображается как компонент {@code library}.
 */
@Component
@RequiredArgsConstructor
public class LibraryHealthIndicator implements HealthIndicator {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    @Override
    public Health health() {
        long books = bookRepository.count();
        Health.Builder builder = books > 0
                ? Health.up()
                : Health.down().withDetail("message", "Каталог книг пуст");

        return builder
                .withDetail("books", books)
                .withDetail("authors", authorRepository.count())
                .withDetail("genres", genreRepository.count())
                .build();
    }
}
