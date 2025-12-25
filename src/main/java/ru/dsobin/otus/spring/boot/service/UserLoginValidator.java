package ru.dsobin.otus.spring.boot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.dsobin.otus.spring.boot.dto.jwt.LoginRequest;
import ru.dsobin.otus.spring.boot.model.User;
import ru.dsobin.otus.spring.boot.repository.UserRepository;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserLoginValidator implements Validator {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    private final CustomUserDetailsService userDetailsService;


    @Override
    public boolean supports(@Nullable Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(@Nullable Object target, @Nullable Errors errors) {
        LoginRequest account = (LoginRequest) target;
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(Objects.requireNonNull(account).getUsername()
                        .toLowerCase(), account.getPassword());
        try {
            authenticationManager.authenticate(authToken);
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (AuthenticationException ex) {
            Objects.requireNonNull(errors)
                    .rejectValue("password", "Неправильное имя пользователя или пароль!");
        }
    }

    public User findUserByUserName(String login) {
        Optional<User> account = userRepository.findByUsername(login);
        if (account.isPresent()) {
            return account.get();
        } else {
            throw new IllegalArgumentException("user not found!");
        }
    }
}
