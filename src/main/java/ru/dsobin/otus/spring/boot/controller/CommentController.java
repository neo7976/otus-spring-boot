package ru.dsobin.otus.spring.boot.controller;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dsobin.otus.spring.boot.dto.BookDto;
import ru.dsobin.otus.spring.boot.dto.CommentDto;
import ru.dsobin.otus.spring.boot.dto.result.ResultDto;
import ru.dsobin.otus.spring.boot.service.CommentService;
import ru.dsobin.otus.spring.boot.utils.ResultUtil;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/comment/api/v1", produces = "text/plain; charset=UTF-8")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping(value = "add-comment/{book_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultDto<CommentDto>> createBook(
            @PathVariable("book_id") Long bookId,
            @RequestBody @NotNull CommentDto dto,
            HttpServletRequest request) {
        // Получаем текущего пользователя из Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName(); // логин пользователя

        CommentDto comment = commentService.create(bookId, dto.getText(), username);
        return ResponseEntity.ok(ResultUtil.createSuccess(comment));
    }
}
