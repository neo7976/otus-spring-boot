package ru.dsobin.otus.rating.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dsobin.otus.rating.dto.RateRequestDto;
import ru.dsobin.otus.rating.dto.RatingSummaryDto;
import ru.dsobin.otus.rating.model.Rating;
import ru.dsobin.otus.rating.repository.RatingRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    @Transactional(readOnly = true)
    public RatingSummaryDto getSummary(Long bookId) {
        Double average = ratingRepository.findAverageScore(bookId);
        long count = ratingRepository.countByBookId(bookId);
        return new RatingSummaryDto(bookId, average == null ? null : Math.round(average * 10) / 10.0, count);
    }

    @Transactional
    public RatingSummaryDto rate(RateRequestDto request) {
        Rating rating = ratingRepository.findByBookIdAndUsername(request.getBookId(), request.getUsername())
                .orElseGet(() -> new Rating(request.getBookId(), request.getUsername()));
        rating.setScore(request.getScore());
        rating.setUpdatedAt(LocalDateTime.now());
        ratingRepository.save(rating);
        return getSummary(request.getBookId());
    }
}
