package ru.dsobin.otus.spring.boot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dsobin.otus.spring.boot.dto.CommentDto;
import ru.dsobin.otus.spring.boot.mapper.CommentMapper;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.model.Comment;
import ru.dsobin.otus.spring.boot.model.User;
import ru.dsobin.otus.spring.boot.repository.BookRepository;
import ru.dsobin.otus.spring.boot.repository.CommentRepository;
import ru.dsobin.otus.spring.boot.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Comment> findByBookId(Long bookId) {
        return commentRepository.findByBookId(bookId);
    }

    @Transactional
    public CommentDto create(Long bookId, String text, String username) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        Comment comment = new Comment();
        comment.setText(text);
        comment.setBook(book);
        comment.setUser(user);
        return Optional.of(commentRepository.save(comment))
                .map(CommentMapper::toDto)
                .orElseThrow(()-> new RuntimeException("Ошибка добавления комментария"));
    }

    @Transactional
    public void update(Long id, String text) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        comment.setText(text);
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }
}
