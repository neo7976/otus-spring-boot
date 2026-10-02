package ru.dsobin.otus.spring.boot.client.rating;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.dsobin.otus.spring.boot.dto.rating.RateRequestDto;
import ru.dsobin.otus.spring.boot.dto.rating.RatingSummaryDto;

@FeignClient(name = "rating-service", url = "${rating-service.url}", fallbackFactory = RatingClientFallbackFactory.class)
public interface RatingClient {

    @GetMapping("/api/v1/ratings/{bookId}/summary")
    RatingSummaryDto getSummary(@PathVariable("bookId") Long bookId);

    @PostMapping("/api/v1/ratings")
    RatingSummaryDto rate(@RequestBody RateRequestDto request);
}
