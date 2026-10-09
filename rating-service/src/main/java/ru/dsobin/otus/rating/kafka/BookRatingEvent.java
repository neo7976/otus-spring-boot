package ru.dsobin.otus.rating.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Событие «пользователь оценил книгу» из топика book-ratings (публикует book-service).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRatingEvent {
    private Long bookId;
    private String username;
    private int score;
}
