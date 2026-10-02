package ru.dsobin.otus.spring.boot.controller;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dsobin.otus.spring.boot.dto.CommentDto;
import ru.dsobin.otus.spring.boot.dto.result.ResultDto;
import ru.dsobin.otus.spring.boot.service.CommentService;
import ru.dsobin.otus.spring.boot.utils.ResultUtil;

@RestController
@RequestMapping(value = "/comment/api/v1", produces = "text/plain; charset=UTF-8")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping(value = "add-comment/{book_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultDto<CommentDto>> createComment(
            @PathVariable("book_id") Long bookId,
            @RequestBody @NotNull CommentDto dto,
            @AuthenticationPrincipal UserDetails user) {
        CommentDto comment = commentService.create(bookId, dto.getText(), user.getUsername());
        return ResponseEntity.ok(ResultUtil.createSuccess(comment));
    }
}
