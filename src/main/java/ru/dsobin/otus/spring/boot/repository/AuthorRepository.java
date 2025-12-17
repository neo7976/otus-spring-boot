package ru.dsobin.otus.spring.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.dsobin.otus.spring.boot.model.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
}
