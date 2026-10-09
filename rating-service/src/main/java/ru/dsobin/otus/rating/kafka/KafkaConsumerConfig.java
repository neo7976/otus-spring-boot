package ru.dsobin.otus.rating.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import ru.dsobin.kafka.config.BaseKafkaConsumerConfig;

@EnableKafka
@Configuration
public class KafkaConsumerConfig extends BaseKafkaConsumerConfig {

    @Value("${kafka.consumer.book-ratings-group-id}")
    private String bookRatingsGroupId;

    @Bean
    public ConsumerFactory<String, BookRatingEvent> bookRatingConsumerFactory() {
        return createConsumerFactory(bookRatingsGroupId, BookRatingEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BookRatingEvent> bookRatingListenerContainerFactory(
            ConsumerFactory<String, BookRatingEvent> bookRatingConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, BookRatingEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factoryBuilder(bookRatingConsumerFactory, factory);
        return factory;
    }
}
