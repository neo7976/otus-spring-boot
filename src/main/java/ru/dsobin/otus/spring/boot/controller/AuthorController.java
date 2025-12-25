package ru.dsobin.otus.spring.boot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dsobin.otus.spring.boot.dto.AuthorDto;
import ru.dsobin.otus.spring.boot.service.AuthorService;

import java.util.List;

@RestController
@RequestMapping("/author/api/v1")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuthorDto>> findAll(
    ) {
        List<AuthorDto> all = authorService.findAll();
        return ResponseEntity.ok(all);
    }
}
