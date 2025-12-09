package ru.dsobin.otus.spring.boot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.repository.AuthorRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Transactional(readOnly = true)
    public Author findById(Long id) {
        return authorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Author not found"));
    }
}
