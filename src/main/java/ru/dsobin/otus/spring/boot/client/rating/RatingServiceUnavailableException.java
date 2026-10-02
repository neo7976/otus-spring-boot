package ru.dsobin.otus.spring.boot.client.rating;

public class RatingServiceUnavailableException extends RuntimeException {

    public RatingServiceUnavailableException() {
        super("Сервис рейтингов временно недоступен");
    }
}
