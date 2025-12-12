package ru.dsobin.otus.spring.boot.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.dsobin.otus.spring.boot.model.User;

import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class JWTUtil {
    private static final String USER_DETAILS = "User details";
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(JWTUtil.class);

    @Value("${jwt.app_name}")
    private String appName;

    @Value("${jwt.jwt_secret}")
    private String secret;

    private static final String USER_NAME = "username";

    private static final int TOKEN_LIFE_IN_MINUTES = 10;

    private static final Logger mLogger = Logger.getLogger(JWTUtil.class.getName());

    public Map<String, Claim> validateTokenAndRetrieveClaim(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(base64ToString(secret)))
                .withSubject(USER_DETAILS)
                .withIssuer(appName)
                .build();

        DecodedJWT jwt = verifier.verify(token);

        Date expirationDate = jwt.getExpiresAt();
        log.info("Expiration date: {}", expirationDate);
        if (expirationDate == null || expirationDate.before(new Date())) {
            throw new JWTVerificationException("Token has expired");
        }
        return jwt.getClaims();
    }


    private static String base64ToString(String strBase) {
        if (!strBase.trim().isEmpty()) {
            try {
                return new String(Base64.getDecoder().decode(strBase.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            } catch (NullPointerException ex) {
                mLogger.log(Level.SEVERE, "String conversion from Base64 error (NullPointerException)");
            }
        }
        return "";
    }

    public String generateToken(User user) {
        return generateToken(user.getUsername(), user.getId(), TOKEN_LIFE_IN_MINUTES, user.getRoles());
    }

    public String generateToken(@NonNull String username, @NonNull Long userId, int minuteTime, Collection<? extends GrantedAuthority> authorities) {
        ZonedDateTime expirationDateTime = ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(minuteTime);
        Date expirationDate = Date.from(expirationDateTime.toInstant());
        List<String> list = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        return JWT.create()
                .withSubject(USER_DETAILS)
                .withClaim(USER_NAME, username)
                .withClaim("userId", userId.toString()) // Добавляем ID пользователя
                .withIssuedAt(new Date())
                .withIssuer(appName)
                .withExpiresAt(expirationDate)
                .withClaim("authorities", list)
                .sign(Algorithm.HMAC256(base64ToString(secret)));
    }
}
