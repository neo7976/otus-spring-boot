package ru.dsobin.otus.spring.boot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = "commentId")
public class CommentDto {
    private Long commentId;
    private String text;
    private String createAt;
}
