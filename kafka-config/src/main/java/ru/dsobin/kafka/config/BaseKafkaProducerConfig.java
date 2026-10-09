package ru.dsobin.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Базовая конфигурация продюсера: ключ — строка, значение — JSON.
 */
public abstract class BaseKafkaProducerConfig {
    @Value("${kafka.bootstrap-servers}")
    protected String servers;

    @Value("${kafka.producer.acks}")
    protected String acks;

    @Value("${kafka.producer.retries}")
    protected int retries;

    @Value("${kafka.producer.properties.max.block.ms}")
    protected int maxBlockMs;

    @Value("${kafka.producer.properties.request.timeout.ms}")
    protected int requestTimeoutMs;

    @Value("${kafka.producer.properties.delivery.timeout.ms}")
    protected int deliveryTimeoutMs;

    protected <T> ProducerFactory<String, T> createProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // Без заголовка с Java-классом: консьюмеры десериализуют в свой тип, общий только JSON
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        props.put(ProducerConfig.ACKS_CONFIG, acks);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockMs);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMs);
        return new DefaultKafkaProducerFactory<>(props);
    }
}
