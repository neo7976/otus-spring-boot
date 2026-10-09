package ru.dsobin.otus.spring.boot.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.dsobin.kafka.producer.BaseKafkaProducer;

@Component
public class BookRatingEventProducer extends BaseKafkaProducer<BookRatingEvent> {

    public BookRatingEventProducer(KafkaTemplate<String, BookRatingEvent> bookRatingKafkaTemplate,
                                   @Value("${kafka.topic.book-ratings}") String topic) {
        super(bookRatingKafkaTemplate, topic);
    }

    /**
     * Ключ — id книги: все оценки одной книги попадают в одну партицию и обрабатываются по порядку.
     */
    public void send(BookRatingEvent event) {
        sendWithKey(String.valueOf(event.getBookId()), event);
    }
}
