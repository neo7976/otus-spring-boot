package ru.dsobin.otus.spring.boot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.dsobin.otus.spring.boot.dto.jwt.JwtResponseDto;
import ru.dsobin.otus.spring.boot.dto.jwt.LoginRequest;
import ru.dsobin.otus.spring.boot.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    /**
     * Проверка логина/пароля делегируется Spring Security (DaoAuthenticationProvider).
     * При неверных данных бросается {@link org.springframework.security.core.AuthenticationException}.
     */
    public JwtResponseDto login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails user = (UserDetails) authentication.getPrincipal();
        return new JwtResponseDto(jwtService.generateToken(user), "Bearer", jwtService.getExpirationSeconds());
    }
}
