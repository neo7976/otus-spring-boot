package ru.dsobin.otus.spring.boot.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponseDto {
    private String token;
    private String tokenType;
    /**
     * Время жизни токена в секундах
     */
    private long expiresIn;
}
