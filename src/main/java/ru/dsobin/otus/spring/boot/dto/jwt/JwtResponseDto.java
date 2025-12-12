package ru.dsobin.otus.spring.boot.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponseDto {
    private String status;
    private String message;
    private String token;
    private Long id;
}