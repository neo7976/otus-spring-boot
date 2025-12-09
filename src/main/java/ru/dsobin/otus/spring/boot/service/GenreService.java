package ru.dsobin.otus.spring.boot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Genre;
import ru.dsobin.otus.spring.boot.repository.AuthorRepository;
import ru.dsobin.otus.spring.boot.repository.GenreRepository;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    @Transactional(readOnly = true)
    public Genre findById(Long id) {
        return genreRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Genre not found"));
    }
}
