package ru.dsobin.otus.spring.boot.controller;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Оценка через Kafka: эндпоинт публикует событие в топик book-ratings.
 */
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = "book-ratings", bootstrapServersProperty = "kafka.bootstrap-servers")
class BookRatingAsyncTest {

    private static final String TOPIC = "book-ratings";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private EmbeddedKafkaBroker broker;

    private Consumer<String, String> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> props = KafkaTestUtils.consumerProps("test-" + System.nanoTime(), "true", broker);
        props.put("auto.offset.reset", "earliest");
        consumer = new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new StringDeserializer())
                .createConsumer();
        broker.consumeFromAnEmbeddedTopic(consumer, TOPIC);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    @DisplayName("Оценка публикуется в Kafka от имени текущего пользователя, ключ — id книги")
    void rateAsyncPublishesEvent() throws Exception {
        mvc.perform(post("/book/api/v1/2/rating/async").with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\":4}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.success").value(true));

        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, TOPIC, 10_000);
        assertThat(record.key()).isEqualTo("2");
        assertThat(record.value()).isEqualTo("{\"bookId\":2,\"username\":\"user\",\"score\":4}");
        // Без заголовка с Java-классом: rating-service десериализует в свой тип
        assertThat(record.headers().lastHeader("__TypeId__")).isNull();
    }

    @Test
    @DisplayName("Некорректная оценка отклоняется до отправки в Kafka")
    void invalidScoreIsNotPublished() throws Exception {
        mvc.perform(post("/book/api/v1/2/rating/async").with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\":9}"))
                .andExpect(status().isBadRequest());

        assertThat(KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(2).toMillis()).count()).isZero();
    }
}
