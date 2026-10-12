package ru.dsobin.otus.spring.boot;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Изоляция внешних вызовов: rating-service и Kafka недоступны (порт 1 — соединение сразу отклоняется).
 * Приложение продолжает отвечать, а circuit breaker'ы размыкаются после серии ошибок.
 */
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(properties = {
        "rating-service.url=http://localhost:1",
        "kafka.bootstrap-servers=localhost:1",
        "kafka.producer.properties.max.block.ms=300"
})
class ResilienceTest {

    /**
     * minimum-number-of-calls из конфигурации default
     */
    private static final int CALLS_TO_OPEN = 5;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CircuitBreakerRegistry registry;

    @BeforeEach
    void resetBreakers() {
        registry.getAllCircuitBreakers().forEach(CircuitBreaker::reset);
    }

    @Test
    @DisplayName("Kafka недоступна: 503, после серии ошибок breaker OPEN и вызовы до брокера не доходят")
    void kafkaProducerCircuitBreakerOpens() throws Exception {
        for (int i = 0; i < CALLS_TO_OPEN; i++) {
            rateAsync().andExpect(status().isServiceUnavailable());
        }

        CircuitBreaker breaker = registry.circuitBreaker("kafkaProducer");
        assertThat(breaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        long start = System.currentTimeMillis();
        rateAsync()
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.msg").value("Брокер сообщений недоступен, повторите позже"));
        assertThat(System.currentTimeMillis() - start)
                .as("при разомкнутом breaker'е таймаут продюсера не ждём")
                .isLessThan(300);
        assertThat(breaker.getMetrics().getNumberOfNotPermittedCalls()).isEqualTo(1);
    }

    @Test
    @DisplayName("rating-service недоступен: книга отдаётся через fallback, breaker Feign-клиента OPEN")
    void feignCircuitBreakerOpens() throws Exception {
        for (int i = 0; i < CALLS_TO_OPEN; i++) {
            mvc.perform(MockMvcRequestBuilders.get("/book/api/v1/1").with(user("user").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.value.title").exists())
                    .andExpect(jsonPath("$.value.rating.available").value(false));
        }

        CircuitBreaker breaker = feignBreaker("getSummary");
        assertThat(breaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    @DisplayName("Состояние circuit breaker'ов видно в /actuator/health, общий статус остаётся UP")
    void circuitBreakersInHealth() throws Exception {
        for (int i = 0; i < CALLS_TO_OPEN; i++) {
            rateAsync();
        }

        mvc.perform(MockMvcRequestBuilders.get("/actuator/health").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components.circuitBreakers.details.kafkaProducer.details.state").value("OPEN"));
    }

    private org.springframework.test.web.servlet.ResultActions rateAsync() throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post("/book/api/v1/1/rating/async")
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"score\":4}"));
    }

    /**
     * Имена breaker'ов Feign-клиента генерирует Spring Cloud: «RatingClient#getSummary(Long)».
     */
    private CircuitBreaker feignBreaker(String method) {
        return registry.getAllCircuitBreakers().toJavaStream()
                .filter(cb -> cb.getName().contains("RatingClient") && cb.getName().contains(method))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Нет breaker'а для RatingClient." + method + ", есть: "
                        + registry.getAllCircuitBreakers().map(CircuitBreaker::getName).toJavaList()));
    }
}
