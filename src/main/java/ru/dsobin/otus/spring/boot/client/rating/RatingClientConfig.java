package ru.dsobin.otus.spring.boot.client.rating;

import feign.Retryer;
import org.springframework.context.annotation.Bean;

/**
 * Настройки только для {@link RatingClient}. Намеренно без {@code @Configuration}:
 * иначе бины попали бы в общий контекст и применились ко всем Feign-клиентам.
 */
public class RatingClientConfig {

    /**
     * Одна повторная попытка через 100 мс при сетевой ошибке (connection refused, timeout),
     * затем срабатывает circuit breaker / fallback.
     * Оба вызова идемпотентны: оценка — upsert по паре книга + пользователь.
     */
    @Bean
    public Retryer ratingClientRetryer() {
        return new Retryer.Default(100, 500, 2);
    }
}
