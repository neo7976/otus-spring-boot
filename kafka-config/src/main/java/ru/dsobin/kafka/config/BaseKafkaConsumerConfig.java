package ru.dsobin.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.BatchErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.RecoveringBatchErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import ru.dsobin.kafka.mapper.MessageDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Базовая конфигурация консьюмера: JSON-сообщения, batch-listener, ручной commit offset'ов.
 * Наследник объявляет бины ConsumerFactory и ConcurrentKafkaListenerContainerFactory под свой тип сообщения.
 */
@Slf4j
public abstract class BaseKafkaConsumerConfig {
    @Value("${kafka.bootstrap-servers}")
    protected String servers;

    @Value("${kafka.consumer.auto-offset-reset:earliest}")
    protected String autoOffsetReset;

    @Value("${kafka.consumer.session-timeout-ms:15000}")
    protected String sessionTimeout;

    @Value("${kafka.consumer.max-partition-fetch-bytes:300000}")
    protected String maxPartitionFetchBytes;

    @Value("${kafka.consumer.max-poll-records:5}")
    protected String maxPollRecords;

    @Value("${kafka.consumer.max-poll-interval-ms:300000}")
    protected String maxPollIntervalMs;

    protected <T> ConsumerFactory<String, T> createConsumerFactory(String groupId, Class<T> targetType) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
        // Тип сообщения берём из конфигурации, а не из заголовков продюсера:
        // у сервисов свои классы DTO, общий только формат JSON
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, targetType.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    protected <T> void factoryBuilder(ConsumerFactory<String, T> consumerFactory,
                                      ConcurrentKafkaListenerContainerFactory<String, T> factory) {
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setPollTimeout(5000);
        factory.getContainerProperties().setMicrometerEnabled(true);
        factory.setBatchErrorHandler(errorHandler());
    }

    /**
     * 3 повтора с паузой 1 c, затем сообщение логируется и пропускается.
     */
    protected BatchErrorHandler errorHandler() {
        return new RecoveringBatchErrorHandler(
                (record, ex) -> log.error("Сообщение пропущено после повторов: topic={}, offset={}: {}",
                        record.topic(), record.offset(), ex.getMessage()),
                new FixedBackOff(1000, 3));
    }
}
