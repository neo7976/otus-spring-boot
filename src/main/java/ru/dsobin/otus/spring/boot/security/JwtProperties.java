package ru.dsobin.otus.spring.boot.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Настройки JWT из application.yml (префикс {@code jwt}).
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Секрет для подписи HS256 в Base64 (не короче 256 бит).
     */
    private String secret;

    /**
     * Издатель токена (claim {@code iss}).
     */
    private String issuer;

    /**
     * Время жизни access-токена.
     */
    private Duration expiration = Duration.ofMinutes(30);
}
