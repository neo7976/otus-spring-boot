package ru.dsobin.otus.spring.boot.controller;

// src/main/java/ru/dsobin/otus/spring/boot/controller/BookViewController.java

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.dsobin.otus.spring.boot.dto.AuthorDto;
import ru.dsobin.otus.spring.boot.dto.GenreDto;
import ru.dsobin.otus.spring.boot.service.AuthorService;
import ru.dsobin.otus.spring.boot.service.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookViewController {

    private final AuthorService authorService;
    private final GenreService genreService;

    @GetMapping("/book/list")
    public String bookListPage(Model model) {
        List<AuthorDto> authors = authorService.findAll();
        List<GenreDto> genres = genreService.findAll();

        model.addAttribute("authors", authors);
        model.addAttribute("genres", genres);

        return "book/list";
    }
}