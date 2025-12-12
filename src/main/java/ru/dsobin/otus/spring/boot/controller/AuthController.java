package ru.dsobin.otus.spring.boot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dsobin.otus.spring.boot.dto.jwt.JwtResponseDto;
import ru.dsobin.otus.spring.boot.dto.jwt.LoginRequest;
import ru.dsobin.otus.spring.boot.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody LoginRequest authRequest, BindingResult bindingResult) {
        JwtResponseDto token = authService.login(authRequest);

        if (token.getToken() == null) {
            return ResponseEntity.badRequest().body(token);
        }

        return ResponseEntity.ok(token);
    }
}
