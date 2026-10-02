package ru.dsobin.otus.spring.boot.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Взаимодействие с rating-service через Feign. Сам сервис заменён WireMock-заглушкой.
 */
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(properties = "rating-service.url=http://localhost:${wiremock.server.port}")
class BookRatingTest {

    private static final String SUMMARY_URL = "/api/v1/ratings/1/summary";
    private static final String RATE_URL = "/api/v1/ratings";

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void resetWireMock() {
        // Сбрасывает заглушки и журнал запросов, чтобы verify() видел только запросы текущего теста
        WireMock.reset();
    }

    @Test
    @DisplayName("Книга отдаётся вместе с рейтингом из rating-service")
    void bookWithRating() throws Exception {
        stubFor(get(urlEqualTo(SUMMARY_URL))
                .willReturn(okJson("{\"bookId\":1,\"average\":4.5,\"count\":2}")));

        mvc.perform(MockMvcRequestBuilders
                        .get("/book/api/v1/1").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value.title").exists())
                .andExpect(jsonPath("$.value.rating.average").value(4.5))
                .andExpect(jsonPath("$.value.rating.count").value(2))
                .andExpect(jsonPath("$.value.rating.available").value(true));
    }

    @Test
    @DisplayName("rating-service отвечает ошибкой — fallback: книга отдаётся, рейтинг помечен недоступным")
    void fallbackWhenRatingServiceFails() throws Exception {
        stubFor(get(urlEqualTo(SUMMARY_URL))
                .willReturn(aResponse().withStatus(500)));

        mvc.perform(MockMvcRequestBuilders
                        .get("/book/api/v1/1").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value.title").exists())
                .andExpect(jsonPath("$.value.rating.available").value(false))
                .andExpect(jsonPath("$.value.rating.average").doesNotExist());
    }

    @Test
    @DisplayName("Оценка уходит в rating-service от имени текущего пользователя")
    void rateBook() throws Exception {
        stubFor(post(urlEqualTo(RATE_URL))
                .willReturn(okJson("{\"bookId\":1,\"average\":5.0,\"count\":1}")));

        mvc.perform(MockMvcRequestBuilders
                        .post("/book/api/v1/1/rating").with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value.average").value(5.0));

        verify(postRequestedFor(urlEqualTo(RATE_URL))
                .withRequestBody(equalToJson("{\"bookId\":1,\"username\":\"user\",\"score\":5}")));
    }

    @Test
    @DisplayName("Оценка при недоступном rating-service — 503")
    void rateBookWhenRatingServiceFails() throws Exception {
        stubFor(post(urlEqualTo(RATE_URL))
                .willReturn(aResponse().withStatus(500)));

        mvc.perform(MockMvcRequestBuilders
                        .post("/book/api/v1/1/rating").with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\":5}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Оценка вне диапазона 1..5 отклоняется без обращения к rating-service")
    void invalidScore() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/book/api/v1/1/rating").with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\":9}"))
                .andExpect(status().isBadRequest());

        verify(0, postRequestedFor(urlEqualTo(RATE_URL)));
    }
}
