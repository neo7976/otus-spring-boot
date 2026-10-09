package ru.dsobin.otus.spring.boot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.dsobin.otus.spring.boot.client.rating.RatingClient;
import ru.dsobin.otus.spring.boot.client.rating.RatingServiceUnavailableException;
import ru.dsobin.otus.spring.boot.dto.rating.RateRequestDto;
import ru.dsobin.otus.spring.boot.dto.rating.RatingSummaryDto;
import ru.dsobin.otus.spring.boot.kafka.BookRatingEvent;
import ru.dsobin.otus.spring.boot.kafka.BookRatingEventProducer;
import ru.dsobin.otus.spring.boot.repository.BookRepository;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class BookRatingService {

    private final RatingClient ratingClient;

    private final BookRepository bookRepository;

    private final BookRatingEventProducer bookRatingEventProducer;

    public RatingSummaryDto getSummary(Long bookId) {
        return ratingClient.getSummary(bookId);
    }

    public RatingSummaryDto rate(Long bookId, String username, int score) {
        validate(bookId, score);
        RatingSummaryDto rating = ratingClient.rate(new RateRequestDto(bookId, username, score));
        if (!rating.isAvailable()) {
            // Сработал fallback: оценка не сохранена
            throw new RatingServiceUnavailableException();
        }
        return rating;
    }

    public void rateAsync(Long bookId, String username, int score) {
        validate(bookId, score);
        bookRatingEventProducer.send(new BookRatingEvent(bookId, username, score));
    }

    private void validate(Long bookId, int score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("Оценка должна быть от 1 до 5");
        }
        if (!bookRepository.existsById(bookId)) {
            throw new EntityNotFoundException("Книга не найдена: " + bookId);
        }
    }
}
