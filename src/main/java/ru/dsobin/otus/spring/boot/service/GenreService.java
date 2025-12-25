package ru.dsobin.otus.spring.boot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dsobin.otus.spring.boot.dto.GenreDto;
import ru.dsobin.otus.spring.boot.mapper.GenreMapper;
import ru.dsobin.otus.spring.boot.model.Genre;
import ru.dsobin.otus.spring.boot.repository.GenreRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    @Transactional(readOnly = true)
    public Genre findById(Long id) {
        return genreRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Genre not found"));
    }

    @Transactional(readOnly = true)
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream().map(GenreMapper::toDto).collect(Collectors.toList());
    }
}
