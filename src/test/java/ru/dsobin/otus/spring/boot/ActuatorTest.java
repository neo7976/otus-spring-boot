package ru.dsobin.otus.spring.boot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
class ActuatorTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("health доступен без авторизации, но без деталей")
    void healthIsPublicWithoutDetails() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components").doesNotExist());
    }

    @Test
    @DisplayName("ADMIN видит детали health, включая собственный индикатор library")
    void adminSeesLibraryHealthDetails() throws Exception {
        mvc.perform(get("/actuator/health").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.library.status").value("UP"))
                .andExpect(jsonPath("$.components.library.details.books").value(greaterThan(0)))
                .andExpect(jsonPath("$.components.db.status").value("UP"));
    }

    @Test
    @DisplayName("Отдельный health-индикатор library")
    void libraryHealthComponent() throws Exception {
        mvc.perform(get("/actuator/health/library").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("metrics: без токена 401, для USER 403, для ADMIN 200")
    void metricsRequireAdmin() throws Exception {
        mvc.perform(get("/actuator/metrics"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/actuator/metrics").with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/actuator/metrics/library.books.count").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.measurements[0].value").value(greaterThan(0.0)));
    }

    @Test
    @DisplayName("logfile отдаёт содержимое лог-файла администратору")
    void logfileIsAvailableForAdmin() throws Exception {
        mvc.perform(get("/actuator/logfile").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("info доступен без авторизации")
    void infoIsPublic() throws Exception {
        mvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app.name").value("otus-spring-boot"));
    }
}
