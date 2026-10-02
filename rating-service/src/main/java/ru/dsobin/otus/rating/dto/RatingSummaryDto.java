package ru.dsobin.otus.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingSummaryDto {

    private Long bookId;

    /**
     * Средняя оценка, округлённая до десятых; null, если оценок нет.
     */
    private Double average;

    private long count;
}
