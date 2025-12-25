package ru.dsobin.otus.spring.boot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dsobin.otus.spring.boot.dto.AuthorDto;
import ru.dsobin.otus.spring.boot.dto.GenreDto;
import ru.dsobin.otus.spring.boot.service.AuthorService;
import ru.dsobin.otus.spring.boot.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genre/api/v1")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GenreDto>> findAll(
    ) {
        List<GenreDto> all = genreService.findAll();
        return ResponseEntity.ok(all);
    }
}
