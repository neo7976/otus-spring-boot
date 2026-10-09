package ru.dsobin.otus.rating;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "kafka.consumer.auto-startup=false")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RatingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Сводный рейтинг считается по стартовым оценкам")
    void summary() throws Exception {
        mvc.perform(get("/api/v1/ratings/1/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.average").value(4.5))
                .andExpect(jsonPath("$.count").value(2));
    }

    @Test
    @DisplayName("Книга без оценок: average = null, count = 0")
    void summaryWithoutRatings() throws Exception {
        mvc.perform(get("/api/v1/ratings/3/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.average").doesNotExist())
                .andExpect(jsonPath("$.count").value(0));
    }

    @Test
    @DisplayName("Повторная оценка того же пользователя заменяет предыдущую")
    void rateTwiceReplacesScore() throws Exception {
        rate("{\"bookId\":3,\"username\":\"user\",\"score\":2}")
                .andExpect(jsonPath("$.average").value(2.0))
                .andExpect(jsonPath("$.count").value(1));
        rate("{\"bookId\":3,\"username\":\"user\",\"score\":4}")
                .andExpect(jsonPath("$.average").value(4.0))
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    @DisplayName("Оценка вне диапазона 1..5 отклоняется")
    void invalidScore() throws Exception {
        mvc.perform(post("/api/v1/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":1,\"username\":\"user\",\"score\":7}"))
                .andExpect(status().isBadRequest());
    }

    private org.springframework.test.web.servlet.ResultActions rate(String body) throws Exception {
        return mvc.perform(post("/api/v1/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }
}
