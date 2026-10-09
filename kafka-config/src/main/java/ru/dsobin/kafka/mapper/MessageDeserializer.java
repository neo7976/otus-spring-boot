package ru.dsobin.kafka.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.nio.charset.StandardCharsets;

/**
 * JSON-десериализатор, который не роняет консьюмер на битом сообщении: логирует и возвращает null.
 */
@Slf4j
public class MessageDeserializer<T> extends JsonDeserializer<T> {

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        try {
            return super.deserialize(topic, headers, data);
        } catch (Exception e) {
            log.error("Error deserializing!!! {}", new String(data, StandardCharsets.UTF_8), e);
            return null;
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            return super.deserialize(topic, data);
        } catch (Exception e) {
            log.error("Error deserializing!!! {}", new String(data, StandardCharsets.UTF_8), e);
            return null;
        }
    }
}
