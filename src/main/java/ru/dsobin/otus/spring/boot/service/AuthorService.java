package ru.dsobin.otus.spring.boot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dsobin.otus.spring.boot.dto.AuthorDto;
import ru.dsobin.otus.spring.boot.mapper.AuthorMapper;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.repository.AuthorRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Transactional(readOnly = true)
    public Author findById(Long id) {
        return authorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Author not found"));
    }
    @Transactional(readOnly = true)
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream().map(AuthorMapper::toDto).collect(Collectors.toList());
    }

}
