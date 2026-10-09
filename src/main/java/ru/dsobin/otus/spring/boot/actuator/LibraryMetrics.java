package ru.dsobin.otus.spring.boot.actuator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import ru.dsobin.otus.spring.boot.repository.BookRepository;

/**
 * Бизнес-метрики библиотеки, доступны в /actuator/metrics/library.books.*
 */
@Component
public class LibraryMetrics {

    private final Counter booksCreated;

    private final Counter booksDeleted;

    public LibraryMetrics(MeterRegistry registry, BookRepository bookRepository) {
        this.booksCreated = Counter.builder("library.books.operations")
                .description("Операции с книгами")
                .tag("operation", "create")
                .register(registry);
        this.booksDeleted = Counter.builder("library.books.operations")
                .description("Операции с книгами")
                .tag("operation", "delete")
                .register(registry);
        Gauge.builder("library.books.count", bookRepository, BookRepository::count)
                .description("Количество книг в каталоге")
                .register(registry);
    }

    public void bookCreated() {
        booksCreated.increment();
    }

    public void bookDeleted() {
        booksDeleted.increment();
    }
}
