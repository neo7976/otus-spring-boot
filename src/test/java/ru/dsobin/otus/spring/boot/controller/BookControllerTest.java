package ru.dsobin.otus.spring.boot.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.dsobin.otus.spring.boot.data.BookData;
import ru.dsobin.otus.spring.boot.dto.BookDto;
import ru.dsobin.otus.spring.boot.dto.result.PageDataDto;
import ru.dsobin.otus.spring.boot.dto.result.ResultDto;
import ru.dsobin.otus.spring.boot.utils.JsonHelperUtils;

import java.nio.charset.Charset;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
        ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
class BookControllerTest {

    @Autowired
    private MockMvc mvc;
    private String bookUrl;

    @BeforeEach
    void setUp() {
        bookUrl = "/book/api/v1";
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Поиск книги по ID")
    void findById() throws Exception {
        String responseBody = mvc.perform(withAdmin(MockMvcRequestBuilders
                        .get(bookUrl + "/1")))
//                        .header(HttpHeaders.AUTHORIZATION, jwtPrefix + adminToken)
//                        .param("user_id", "7"))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse().getContentAsString(Charset.defaultCharset());
        System.out.println(responseBody);
        ResultDto resultDto = JsonHelperUtils.parseJson(responseBody, ResultDto.class);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.isSuccess()).isTrue();
        assertThat(resultDto.getValue()).isNotNull();
    }

    @Test
    @DisplayName("Поиск книги по ID. Ошибка")
    void notFoundById() throws Exception {
        String responseBody = mvc.perform(withAdmin(MockMvcRequestBuilders
                        .get(bookUrl + "/9999")))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse().getContentAsString(Charset.defaultCharset());
        System.out.println(responseBody);
        ResultDto resultDto = JsonHelperUtils.parseJson(responseBody, ResultDto.class);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.isSuccess()).isFalse();
        assertThat(resultDto.getValue()).isNull();
    }

    @Test
    @DisplayName("Поиск книги по ID. Без авторизации блок")
    void redirectFindById() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get(bookUrl + "/9999"))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse().getContentAsString(Charset.defaultCharset());
    }

    @Test
    @DisplayName("Добавить новую книгу и изменить")
    void createAndUpdBook() throws Exception {
        BookDto bookDto = BookData.testDto();
        ResultDto resultDto = createBook(bookDto, MockMvcRequestBuilders
                .post(bookUrl));

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.isSuccess()).isTrue();
        assertThat(resultDto.getValue()).isNotNull();
        Map<String, Object> valueMap = (Map<String, Object>) resultDto.getValue();

        String actualTitle = (String) valueMap.get("title");
        Long bookId = ((Number) valueMap.get("bookId")).longValue();
        assertThat(actualTitle).isEqualTo(bookDto.getTitle());

        Map<String, Object> authorMap = (Map<String, Object>) valueMap.get("author");
        Long actualAuthorId = ((Number) authorMap.get("authorId")).longValue();
        assertThat(actualAuthorId).isEqualTo(bookDto.getAuthor().getAuthorId());

        Map<String, Object> genreMap = (Map<String, Object>) valueMap.get("genre");
        Long actualGenreId = ((Number) genreMap.get("genreId")).longValue();
        assertThat(actualGenreId).isEqualTo(bookDto.getGenre().getGenreId());

        BookDto bookUpdDto = BookData.testUpdDto();
        ResultDto resultUpdDto = createBook(bookUpdDto, MockMvcRequestBuilders
                .put(bookUrl + "/" + bookId));

        assertThat(resultUpdDto).isNotNull();
        assertThat(resultUpdDto.isSuccess()).isTrue();
        assertThat(resultUpdDto.getValue()).isNotNull();
        Map<String, Object> valueMapUpd = (Map<String, Object>) resultUpdDto.getValue();

        String updTitle = (String) valueMapUpd.get("title");
        assertThat(updTitle).isEqualTo(bookUpdDto.getTitle());

        Map<String, Object> updAuthorMap = (Map<String, Object>) valueMapUpd.get("author");
        Long updAuthorId = ((Number) updAuthorMap.get("authorId")).longValue();
        assertThat(updAuthorId).isEqualTo(bookUpdDto.getAuthor().getAuthorId());

        Map<String, Object> updGenreMap = (Map<String, Object>) valueMapUpd.get("genre");
        Long updGenreId = ((Number) updGenreMap.get("genreId")).longValue();
        assertThat(updGenreId).isEqualTo(bookUpdDto.getGenre().getGenreId());
    }

    private ResultDto createBook(BookDto bookDto, MockHttpServletRequestBuilder bookUrl) throws Exception {
        String requestBodyCreate = JsonHelperUtils.getStringFromObject(bookDto);

        String responseBodyCreate = mvc.perform(withAdmin(bookUrl
                        .content(requestBodyCreate)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(Charset.defaultCharset());
        System.out.println(responseBodyCreate);
        ResultDto resultDto = JsonHelperUtils.parseJson(responseBodyCreate, ResultDto.class);
        return resultDto;
    }

    @Test
    @DisplayName("Поиск всех книги")
    void findAll() throws Exception {
        String responseBody = mvc.perform(withAdmin(MockMvcRequestBuilders
                        .get(bookUrl + "")))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse().getContentAsString(Charset.defaultCharset());
        System.out.println(responseBody);
        PageDataDto pageDataDto = JsonHelperUtils.parseJson(responseBody, PageDataDto.class);

        assertThat(pageDataDto).isNotNull();
        assertThat(pageDataDto.getCountPage()).isPositive();
        assertThat(pageDataDto.getData()).isNotEmpty();
    }

    @Test
    @DisplayName("Удалить книгу по ID")
    void deleteById() throws Exception {
        String responseBody = mvc.perform(withAdmin(MockMvcRequestBuilders
                        .delete(bookUrl + "/1")))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse().getContentAsString(Charset.defaultCharset());
        System.out.println(responseBody);
        ResultDto resultDto = JsonHelperUtils.parseJson(responseBody, ResultDto.class);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.isSuccess()).isTrue();
        assertThat(resultDto.getValue()).isEqualTo(Boolean.TRUE);
    }

    @Test
    @DisplayName("Удалить книгу по ID. Ошибка")
    void notDeleteById() throws Exception {
        String responseBody = mvc.perform(withAdmin(MockMvcRequestBuilders
                        .delete(bookUrl + "/9999")))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse().getContentAsString(Charset.defaultCharset());
        System.out.println(responseBody);
        ResultDto resultDto = JsonHelperUtils.parseJson(responseBody, ResultDto.class);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.isSuccess()).isFalse();
        assertThat(resultDto.getValue()).isNull();
    }

    private MockHttpServletRequestBuilder withAdmin(MockHttpServletRequestBuilder builder) {
        return builder.with(user("admin").roles("ADMIN"));
    }
}