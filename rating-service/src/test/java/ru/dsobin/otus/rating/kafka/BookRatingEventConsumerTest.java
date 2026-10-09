package ru.dsobin.otus.rating.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import ru.dsobin.otus.rating.dto.RatingSummaryDto;
import ru.dsobin.otus.rating.service.RatingService;

import java.util.Map;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Сообщения отправляются в топик как «сырой» JSON — так же, как их публикует book-service.
 */
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = "book-ratings", bootstrapServersProperty = "kafka.bootstrap-servers")
class BookRatingEventConsumerTest {

    private static final String TOPIC = "book-ratings";

    @Autowired
    private EmbeddedKafkaBroker broker;

    @Autowired
    private RatingService ratingService;

    private KafkaTemplate<String, String> producer;

    @BeforeEach
    void setUp() {
        Map<String, Object> props = KafkaTestUtils.producerProps(broker);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producer = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
    }

    @AfterEach
    void tearDown() {
        producer.destroy();
    }

    @Test
    @DisplayName("Оценка из Kafka сохраняется, некорректные сообщения пропускаются")
    void consumesRatingEvents() throws Exception {
        // книга 3 в стартовых данных без оценок
        producer.send(TOPIC, "3", "не JSON").get();
        producer.send(TOPIC, "3", "{\"bookId\":3,\"username\":\"user\",\"score\":9}").get();
        producer.send(TOPIC, "3", "{\"bookId\":3,\"username\":\"user\",\"score\":4}").get();

        RatingSummaryDto summary = await(() -> ratingService.getSummary(3L), s -> s.getCount() == 1);

        assertThat(summary.getAverage()).isEqualTo(4.0);
        assertThat(summary.getCount()).isEqualTo(1);
    }

    private static <T> T await(java.util.function.Supplier<T> supplier, Predicate<T> condition) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 15_000;
        T value = supplier.get();
        while (!condition.test(value) && System.currentTimeMillis() < deadline) {
            Thread.sleep(200);
            value = supplier.get();
        }
        return value;
    }
}
