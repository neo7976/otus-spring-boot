package ru.dsobin.otus.spring.boot.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.dsobin.kafka.config.BaseKafkaProducerConfig;

import java.util.Map;

@Configuration
public class KafkaProducerConfig extends BaseKafkaProducerConfig {

    @Value("${kafka.topic.book-ratings}")
    private String bookRatingsTopic;

    @Value("${kafka.topic.auto-create:true}")
    private boolean autoCreateTopics;

    @Bean
    public ProducerFactory<String, BookRatingEvent> bookRatingProducerFactory() {
        return createProducerFactory();
    }

    @Bean
    public KafkaTemplate<String, BookRatingEvent> bookRatingKafkaTemplate(
            ProducerFactory<String, BookRatingEvent> bookRatingProducerFactory) {
        return new KafkaTemplate<>(bookRatingProducerFactory);
    }

    /**
     * KafkaAdmin создаёт топик при старте, если его ещё нет.
     * Если брокер недоступен, приложение всё равно стартует (fatalIfBrokerNotAvailable = false).
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        KafkaAdmin admin = new KafkaAdmin(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, servers));
        admin.setAutoCreate(autoCreateTopics);
        admin.setOperationTimeout(10);
        return admin;
    }

    @Bean
    public NewTopic bookRatingsTopic() {
        return TopicBuilder.name(bookRatingsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
