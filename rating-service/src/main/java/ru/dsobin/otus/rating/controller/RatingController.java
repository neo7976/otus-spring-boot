package ru.dsobin.otus.rating.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dsobin.otus.rating.dto.RateRequestDto;
import ru.dsobin.otus.rating.dto.RatingSummaryDto;
import ru.dsobin.otus.rating.service.RatingService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping("/{bookId}/summary")
    public RatingSummaryDto getSummary(@PathVariable("bookId") Long bookId) {
        return ratingService.getSummary(bookId);
    }

    @PostMapping
    public RatingSummaryDto rate(@Valid @RequestBody RateRequestDto request) {
        return ratingService.rate(request);
    }
}
