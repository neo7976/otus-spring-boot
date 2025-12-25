package ru.dsobin.otus.spring.boot.config.jwt;

import com.auth0.jwt.interfaces.Claim;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJwt {
    private Long id;
    private String username;

    /**
     * Когда получили токен
     */
    private Date issuedAt;
    /**
     * Время жизни токена
     */
    private Date expiresAt;

    private Collection<? extends GrantedAuthority> authorities;

    public static UserJwt create(Map<String, Claim> claims) {
        return UserJwt.builder()
                .id(Long.parseLong(claims.get("userId").asString() == null ? claims.get("userId").toString() : claims.get("userId").asString()))
                .username(claims.get("username").asString())
                .expiresAt(claims.get("exp").asDate())
                .expiresAt(claims.get("exp").asDate())
                .issuedAt(claims.get("iat").asDate())
                .authorities(Optional.ofNullable(claims.get("authorities")).map(x-> x.asList(Permission.class)).orElse(Collections.emptyList()))
                .build();
    }
}
