package ru.dsobin.otus.spring.boot.exception;

import lombok.Getter;

/**
 * Сущность не найдена. Хранит код сообщения и аргументы,
 * чтобы слой представления сам выбрал язык текста через MessageSource.
 */
@Getter
public class EntityNotFoundException extends RuntimeException {

    private final String messageCode;

    private final transient Object[] args;

    public EntityNotFoundException(String messageCode, Object... args) {
        super(messageCode);
        this.messageCode = messageCode;
        this.args = args;
    }
}
