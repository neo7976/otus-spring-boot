package ru.dsobin.otus.spring.boot.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
