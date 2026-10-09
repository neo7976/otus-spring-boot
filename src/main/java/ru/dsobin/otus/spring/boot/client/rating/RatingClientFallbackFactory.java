package ru.dsobin.otus.spring.boot.client.rating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.dsobin.otus.spring.boot.dto.rating.RateRequestDto;
import ru.dsobin.otus.spring.boot.dto.rating.RatingSummaryDto;

@Slf4j
@Component
public class RatingClientFallbackFactory implements FallbackFactory<RatingClient> {

    @Override
    public RatingClient create(Throwable cause) {
        return new RatingClient() {
            @Override
            public RatingSummaryDto getSummary(Long bookId) {
                log.warn("rating-service недоступен, рейтинг книги {} не получен: {}", bookId, cause.toString());
                return RatingSummaryDto.unavailable(bookId);
            }

            @Override
            public RatingSummaryDto rate(RateRequestDto request) {
                log.warn("rating-service недоступен, оценка книги {} не сохранена: {}", request.getBookId(), cause.toString());
                return RatingSummaryDto.unavailable(request.getBookId());
            }
        };
    }
}
