package ru.dsobin.otus.spring.boot.config.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.dsobin.otus.spring.boot.config.UserDetailsServiceImpl;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private static final String USER_NAME = "username";
    private static final String[] IGNORE_URI = new String[]{"login", "register", "forgot", "reset"};
    private static final Logger mLogger = Logger.getLogger(JWTFilter.class.getName());

    private final JWTUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ") &&
                Arrays.stream(IGNORE_URI).noneMatch(request.getRequestURI()::equals)) {

            String jwt = authHeader.substring(7);
            if (jwt.isBlank()) {
                Objects.requireNonNull(response)
                        .sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token in Bearer Header");
            } else {
                try {
                    Map<String, Claim> claims = jwtUtil.validateTokenAndRetrieveClaim(jwt);
                    UserDetailsImpl userDetails = userDetailsServiceImpl.loadUserByClaims(claims);
//                    String userName = claims.get(USER_NAME).asString();
//                    UserDetailsImpl userDetails = userDetailsServiceImpl.loadUserByUsername(userName); //В будущем переделать на обновление токена
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } catch (JWTVerificationException ex) {
                    if (ex.getMessage().contains("expired")) {
                        mLogger.log(Level.SEVERE, "Access Token has expired: {}", ex.getMessage());
                    } else {
                        mLogger.log(Level.SEVERE, "JWT verification failed: {}", ex.getMessage());
                    }

                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Token Expired");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
