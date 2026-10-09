package ru.dsobin.otus.rating.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.dsobin.kafka.consumer.BaseKafkaConsumer;
import ru.dsobin.otus.rating.dto.RateRequestDto;
import ru.dsobin.otus.rating.dto.RatingSummaryDto;
import ru.dsobin.otus.rating.service.RatingService;

import java.util.List;

/**
 * Принимает оценки книг из Kafka и сохраняет их тем же сервисом, что и REST API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookRatingEventConsumer extends BaseKafkaConsumer<BookRatingEvent> {

    private final RatingService ratingService;

    @KafkaListener(
            topics = "${kafka.topic.book-ratings}",
            containerFactory = "bookRatingListenerContainerFactory",
            autoStartup = "${kafka.consumer.auto-startup:true}")
    public void listen(List<ConsumerRecord<String, BookRatingEvent>> records, Acknowledgment acknowledgment) {
        processRecords(records, acknowledgment);
    }

    @Override
    protected void processRecord(ConsumerRecord<String, BookRatingEvent> record) {
        BookRatingEvent event = record.value();
        validateMsg(event);
        if (event.getBookId() == null || event.getUsername() == null || event.getScore() < 1 || event.getScore() > 5) {
            throw new IllegalArgumentException("Некорректная оценка: " + event);
        }
        RatingSummaryDto summary = ratingService.rate(
                new RateRequestDto(event.getBookId(), event.getUsername(), event.getScore()));
        log.info("Оценка из Kafka сохранена: {} -> средняя {} ({} оценок)",
                event, summary.getAverage(), summary.getCount());
    }
}
