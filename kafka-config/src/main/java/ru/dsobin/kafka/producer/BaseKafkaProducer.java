package ru.dsobin.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public abstract class BaseKafkaProducer<T> {
    protected final KafkaTemplate<String, T> template;
    protected final String topic;

    private final static int timeout = 10;
    private final static TimeUnit timeUnit = TimeUnit.SECONDS;

    protected BaseKafkaProducer(KafkaTemplate<String, T> template, String topic) {
        this.template = template;
        this.topic = topic;
    }

    protected void sendTo(T obj, List<Header> incomingHeaders) {
        sendWithKey(UUID.randomUUID().toString(), obj, incomingHeaders);
    }

    protected void sendTo(T obj) {
        sendTo(obj, null);
    }

    protected void sendWithKey(String key, T obj) throws KafkaException {
        sendWithKey(key, obj, (List<Header>) null);
    }

    protected void sendWithKey(String key, T obj, List<Header> incomingHeaders) throws KafkaException {
        log.info("Sending message to topic: {} with key: {}", topic, key);
        try {
            ArrayList<Header> headers = new ArrayList<>();
            
            if (incomingHeaders != null) {
                for (Header header : incomingHeaders) {
                    headers.add(new RecordHeader(header.key(), header.value()));
                }
            }

            ProducerRecord<String, T> record = new ProducerRecord<>(
                    topic,
                    null,
                    key,
                    obj,
                    headers
            );

            template.send(record).get(timeout, timeUnit);
            template.flush();
            log.info("Successfully sent message to topic: {}", topic);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Error sending message to topic {}: {}", topic, e.getMessage(), e);
            throw new KafkaException("Failed to send message to topic: " + topic, e);
        }
    }

    protected void sendWithKey(String key, T obj, Headers headers) {
        log.info("Sending message to topic: {} with key: {}", topic, key);
        try {
            ProducerRecord<String, T> record = new ProducerRecord<>(
                    topic,
                    null,
                    key,
                    obj,
                    headers
            );
            template.send(record).get(timeout, timeUnit);
            template.flush();
            log.info("Successfully sent message to topic: {}", topic);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Error sending message to topic {}: {}", topic, e.getMessage(), e);
            throw new KafkaException("Failed to send message to topic: " + topic, e);
        }
    }
}
