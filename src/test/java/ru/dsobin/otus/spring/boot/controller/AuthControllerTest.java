package ru.dsobin.otus.spring.boot.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.dsobin.otus.spring.boot.dto.jwt.JwtResponseDto;
import ru.dsobin.otus.spring.boot.dto.jwt.LoginRequest;
import ru.dsobin.otus.spring.boot.utils.JsonHelperUtils;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
class AuthControllerTest {

    private static final String LOGIN_URL = "/api/auth/login";
    private static final String BOOK_URL = "/book/api/v1";

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Логин с верным паролем возвращает токен, с которым доступно API")
    void loginAndAccessApiWithToken() throws Exception {
        String token = login("admin", "password");

        assertThat(token).isNotBlank();
        mvc.perform(get(BOOK_URL + "/1").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Логин с неверным паролем - 401")
    void loginWithWrongPassword() throws Exception {
        mvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonHelperUtils.getStringFromObject(new LoginRequest("admin", "wrong"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Невалидный токен - 401")
    void invalidToken() throws Exception {
        mvc.perform(get(BOOK_URL + "/1").header(HttpHeaders.AUTHORIZATION, "Bearer invalid.token.value"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Пользователь без роли ADMIN не может удалить книгу - 403")
    void userCannotDeleteBook() throws Exception {
        String token = login("user", "password");

        mvc.perform(delete(BOOK_URL + "/1").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    private String login(String username, String password) throws Exception {
        String body = mvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonHelperUtils.getStringFromObject(new LoginRequest(username, password))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(StandardCharsets.UTF_8);
        return JsonHelperUtils.parseJson(body, JwtResponseDto.class).getToken();
    }
}
