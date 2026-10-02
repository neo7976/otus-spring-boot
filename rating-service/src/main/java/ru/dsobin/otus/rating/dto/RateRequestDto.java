package ru.dsobin.otus.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateRequestDto {

    @NotNull
    private Long bookId;

    @NotBlank
    private String username;

    @Min(1)
    @Max(5)
    private int score;
}
