package ru.dsobin.otus.spring.boot.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.dsobin.otus.spring.boot.dto.result.ResultDto;
import ru.dsobin.otus.spring.boot.dto.result.ResultStatusDto;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler({RuntimeException.class, EntityNotFoundException.class})
    public ResponseEntity<ResultStatusDto> exceptionHandler(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResultDto<>(false, e.getMessage(), null));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResultStatusDto> unauthorizedExceptionHandler(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResultDto<>(false, "Неверное имя пользователя или пароль", null));
    }

    /**
     * Иначе отказ из {@code @PreAuthorize} попадёт в обработчик RuntimeException и вернётся как 400.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResultStatusDto> accessDeniedExceptionHandler(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ResultDto<>(false, "Недостаточно прав", null));
    }
}
