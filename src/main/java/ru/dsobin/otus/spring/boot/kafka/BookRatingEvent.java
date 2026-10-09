package ru.dsobin.otus.spring.boot.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Событие «пользователь оценил книгу» в топике book-ratings.
 * Контракт между сервисами — JSON-формат; у rating-service свой класс с теми же полями.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRatingEvent {
    private Long bookId;
    private String username;
    private int score;
}
