package ru.dsobin.otus.spring.boot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.dsobin.otus.spring.boot.dto.BookDto;
import ru.dsobin.otus.spring.boot.dto.result.PageDataDto;
import ru.dsobin.otus.spring.boot.dto.result.ResultDto;
import ru.dsobin.otus.spring.boot.filter.BookPageInfoDto;
import ru.dsobin.otus.spring.boot.service.BookService;
import ru.dsobin.otus.spring.boot.utils.ResultUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(value = "/book/api/v1", produces = "text/plain; charset=UTF-8")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultDto<BookDto>> findById(
            @PathVariable("id") Long id,
            HttpServletRequest request) {
        BookDto book = bookService.findById(id);
        return ResponseEntity.ok(ResultUtil.createSuccess(book));
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageDataDto<BookDto>> findAll(
            HttpServletRequest request,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "orderBy", defaultValue = "desc") String orderBy,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "authorId", required = false) Long authorId,
            @RequestParam(value = "genreId", required = false) Long genreId,
            @RequestParam(value = "bookId", required = false) Long bookId,
            @RequestParam(value = "title", required = false) String title
    ) {
        try {
            BookPageInfoDto infoDto = new BookPageInfoDto(sortBy, orderBy, pageNumber, pageSize, authorId, genreId, bookId, title);
            PageDataDto<BookDto> all = bookService.findAll(infoDto);
            return ResponseEntity.ok(all);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new PageDataDto<BookDto>().setStatus(400));
        }
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultDto<BookDto>> createBook(
            @RequestBody @NotNull BookDto dto,
            HttpServletRequest request) {
        BookDto book = bookService.create(dto);
        return ResponseEntity.ok(ResultUtil.createSuccess(book));
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultDto<BookDto>> update(
            @PathVariable("id") Long bookId,
            @RequestBody @NotNull BookDto dto,
            HttpServletRequest request) {
        BookDto book = bookService.update(bookId, dto);
        return ResponseEntity.ok(ResultUtil.createSuccess(book));
    }


    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultDto<Boolean>> deleteById(
            @PathVariable("id") Long id,
            HttpServletRequest request) {
        boolean result = bookService.deleteById(id);
        return ResponseEntity.ok(ResultUtil.createSuccess(result));
    }
}
