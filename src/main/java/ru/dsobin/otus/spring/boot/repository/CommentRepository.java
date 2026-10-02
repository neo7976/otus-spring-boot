package ru.dsobin.otus.spring.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.dsobin.otus.spring.boot.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBookId(Long bookId);
    void deleteByBookId(Long bookId); // для каскадного удаления вручную
}
