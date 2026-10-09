package ru.dsobin.otus.spring.boot.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Сводный рейтинг книги из rating-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingSummaryDto {

    private Long bookId;

    /**
     * Средняя оценка; null, если оценок нет или сервис недоступен.
     */
    private Double average;

    private long count;

    /**
     * false — rating-service не ответил, данные не актуальны (заполняется fallback'ом).
     */
    private boolean available = true;

    public static RatingSummaryDto unavailable(Long bookId) {
        return new RatingSummaryDto(bookId, null, 0, false);
    }
}
