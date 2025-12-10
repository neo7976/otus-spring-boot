package ru.dsobin.otus.spring.boot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.dsobin.otus.spring.boot.model.Book;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @EntityGraph(attributePaths = {"author", "genre", "comments"})
    Optional<Book> findById(Long id);

    @EntityGraph(attributePaths = {"author", "genre", "comments"})
    List<Book> findAll();


    @Override
    @EntityGraph(attributePaths = {"author", "genre", "comments"})
    Page<Book> findAll(Specification<Book> specification, Pageable pageable);
}
