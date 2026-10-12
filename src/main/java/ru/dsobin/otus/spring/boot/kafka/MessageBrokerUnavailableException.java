package ru.dsobin.otus.spring.boot.kafka;

public class MessageBrokerUnavailableException extends RuntimeException {

    public MessageBrokerUnavailableException(Throwable cause) {
        super("Брокер сообщений недоступен, повторите позже", cause);
    }
}
