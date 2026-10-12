package ru.dsobin.otus.spring.boot.kafka;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.dsobin.kafka.producer.BaseKafkaProducer;

@Slf4j
@Component
public class BookRatingEventProducer extends BaseKafkaProducer<BookRatingEvent> {

    public BookRatingEventProducer(KafkaTemplate<String, BookRatingEvent> bookRatingKafkaTemplate,
                                   @Value("${kafka.topic.book-ratings}") String topic) {
        super(bookRatingKafkaTemplate, topic);
    }

    /**
     * Ключ — id книги: все оценки одной книги попадают в одну партицию и обрабатываются по порядку.
     * <p>
     * Вызов брокера изолирован circuit breaker'ом: после серии ошибок/таймаутов breaker размыкается,
     * и следующие вызовы сразу уходят в fallback, не дожидаясь таймаута продюсера.
     */
    @CircuitBreaker(name = "kafkaProducer", fallbackMethod = "sendFallback")
    public void send(BookRatingEvent event) {
        sendWithKey(String.valueOf(event.getBookId()), event);
    }

    /**
     * Оценку нельзя потерять молча, поэтому клиенту сообщаем о недоступности (503).
     */
    private void sendFallback(BookRatingEvent event, Throwable cause) {
        if (cause instanceof CallNotPermittedException) {
            log.warn("Circuit breaker kafkaProducer разомкнут, событие не отправлено: {}", event);
        } else {
            log.warn("Kafka недоступна, событие не отправлено: {}: {}", event, cause.toString());
        }
        throw new MessageBrokerUnavailableException(cause);
    }
}
