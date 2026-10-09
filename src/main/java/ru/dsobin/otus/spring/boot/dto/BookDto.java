package ru.dsobin.otus.spring.boot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.dsobin.otus.spring.boot.dto.rating.RatingSummaryDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = "bookId")
public class BookDto {

    private Long bookId;

    private String title;

    private AuthorDto author;

    private GenreDto genre;

    private List<CommentDto> comments;

    /**
     * Рейтинг из rating-service (заполняется только при запросе одной книги)
     */
    private RatingSummaryDto rating;
}
