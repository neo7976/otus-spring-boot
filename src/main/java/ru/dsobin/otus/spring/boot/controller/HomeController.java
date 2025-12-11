package ru.dsobin.otus.spring.boot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.dsobin.otus.spring.boot.service.BookService;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BookService bookService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/book/list")
    public String bookList(Model model) {
        //todo
//         model.addAttribute("books", bookService.findAll());
        return "book/list";
    }
}
