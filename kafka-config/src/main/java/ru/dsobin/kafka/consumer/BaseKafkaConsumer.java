package ru.dsobin.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;
import java.util.function.Predicate;

@Slf4j
public abstract class BaseKafkaConsumer<T> {

    protected void processRecords(
            List<ConsumerRecord<String, T>> records,
            Acknowledgment acknowledgment,
            Predicate<ConsumerRecord<String, T>> skipPredicate
    ) {
        log.info("Получено новое сообщение размером в {}", records.size());

        String topic = null;
        int successCount = 0;
        int errorCount = 0;

        for (ConsumerRecord<String, T> record : records) {
            try {
                logRecordMetadata(record);
                topic = record.topic();

                if (skipPredicate != null && skipPredicate.test(record)) {
                    log.debug("Пропущено сообщение: topic=[{}], offset=[{}], partition=[{}]",
                            record.topic(), record.offset(), record.partition());
                    continue;
                }

                processRecord(record);
                successCount++;
            } catch (Exception e) {
                errorCount++;
                log.error("Ошибка при обработке сообщения: topic=[{}], offset=[{}], partition=[{}]: {}",
                        record.topic(), record.offset(), record.partition(), e.getMessage(), e);
            }
        }

        log.info("Обработка завершена для topic: [{}]. успешно={}, ошибок={}", topic, successCount, errorCount);
        acknowledgment.acknowledge();
    }

    protected void processRecords(
            List<ConsumerRecord<String, T>> records,
            Acknowledgment acknowledgment
    ) {
        processRecords(records, acknowledgment, null);
    }

    protected void logRecordMetadata(ConsumerRecord<String, T> record) {
        log.debug("Обработка сообщения: topic=[{}], offset=[{}], partition=[{}], ts=[{}], key=[{}]",
                record.topic(), record.offset(), record.partition(), record.timestamp(), record.key());
    }

    protected abstract void processRecord(ConsumerRecord<String, T> record) throws Exception;


    protected void validateMsg(T msg) {
        if (msg == null) {
            log.warn("Пропущена некорректная запись: msg = null");
            throw new IllegalArgumentException("Пропущена некорректная запись: msg = null");
        }
    }
}
