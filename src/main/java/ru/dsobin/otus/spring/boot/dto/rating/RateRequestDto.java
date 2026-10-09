package ru.dsobin.otus.spring.boot.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateRequestDto {
    private Long bookId;
    private String username;
    private int score;
}
