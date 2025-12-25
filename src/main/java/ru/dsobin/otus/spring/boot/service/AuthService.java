package ru.dsobin.otus.spring.boot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.dsobin.otus.spring.boot.config.jwt.JWTUtil;
import ru.dsobin.otus.spring.boot.dto.jwt.JwtResponseDto;
import ru.dsobin.otus.spring.boot.dto.jwt.LoginRequest;
import ru.dsobin.otus.spring.boot.model.User;
import ru.dsobin.otus.spring.boot.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder; // инжектите из SecurityConfig
    private final UserRepository userRepository;


    public JwtResponseDto login(@NonNull LoginRequest authRequest) {
        final String login = authRequest.getUsername().toLowerCase();

        User userDetails = userRepository.findByUsernameIgnoreCase(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + authRequest.getUsername()));

        if (passwordEncoder.matches(authRequest.getPassword(), userDetails.getPassword())) {
            String token = jwtUtil.generateToken(userDetails);
            return new JwtResponseDto("OK", "OK",
                    token,
                    userDetails.getId());

        } else {
            return new JwtResponseDto("Error", "Неправильный пароль", null, null);
        }
    }
}
