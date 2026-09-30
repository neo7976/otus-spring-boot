package ru.dsobin.otus.spring.boot.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Выпуск и проверка JWT (HS256).
 */
@Service
public class JwtService {

    /**
     * Роли кладём в токен только для клиента (показать/скрыть элементы UI).
     * На сервере права берутся из {@link org.springframework.security.core.userdetails.UserDetailsService}.
     */
    private static final String ROLES_CLAIM = "roles";

    private final JwtProperties properties;

    private final Algorithm algorithm;

    private final JWTVerifier verifier;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.algorithm = Algorithm.HMAC256(Base64.getDecoder().decode(properties.getSecret()));
        this.verifier = JWT.require(algorithm)
                .withIssuer(properties.getIssuer())
                .build();
    }

    public String generateToken(UserDetails user) {
        Instant now = Instant.now();
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return JWT.create()
                .withIssuer(properties.getIssuer())
                .withSubject(user.getUsername())
                .withClaim(ROLES_CLAIM, roles)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(properties.getExpiration())))
                .sign(algorithm);
    }

    /**
     * Проверяет подпись, издателя и срок действия токена и возвращает имя пользователя.
     *
     * @throws JWTVerificationException если токен невалиден или истёк
     */
    public String extractUsername(String token) throws JWTVerificationException {
        return verifier.verify(token).getSubject();
    }

    public long getExpirationSeconds() {
        return properties.getExpiration().getSeconds();
    }
}
