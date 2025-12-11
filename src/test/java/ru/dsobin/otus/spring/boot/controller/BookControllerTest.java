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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
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
        String responseBody = mvc.perform(MockMvcRequestBuilders
                        .get(bookUrl + "/1"))
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
        String responseBody = mvc.perform(MockMvcRequestBuilders
                        .get(bookUrl + "/9999"))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse().getContentAsString(Charset.defaultCharset());
        System.out.println(responseBody);
        ResultDto resultDto = JsonHelperUtils.parseJson(responseBody, ResultDto.class);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.isSuccess()).isFalse();
        assertThat(resultDto.getValue()).isNull();
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
        Integer bookId = (Integer)valueMap.get("bookId");
        assertThat(actualTitle).isEqualTo(bookDto.getTitle());

        BookDto bookUpdDto = BookData.testUpdDto();
        ResultDto resultUpdDto = createBook(bookUpdDto, MockMvcRequestBuilders
                .put(bookUrl + "/" + bookId));

        assertThat(resultUpdDto).isNotNull();
        assertThat(resultUpdDto.isSuccess()).isTrue();
        assertThat(resultUpdDto.getValue()).isNotNull();
        Map<String, Object> valueMapUpd = (Map<String, Object>) resultUpdDto.getValue();

        String updTitle = (String) valueMapUpd.get("title");
        assertThat(updTitle).isEqualTo(bookUpdDto.getTitle());
    }

    private ResultDto createBook(BookDto bookDto, MockHttpServletRequestBuilder bookUrl) throws Exception {
        String requestBodyCreate = JsonHelperUtils.getStringFromObject(bookDto);

        String responseBodyCreate = mvc.perform(bookUrl
                        .content(requestBodyCreate)
                        .contentType(MediaType.APPLICATION_JSON))
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
        String responseBody = mvc.perform(MockMvcRequestBuilders
                        .get(bookUrl + ""))
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
        String responseBody = mvc.perform(MockMvcRequestBuilders
                        .delete(bookUrl + "/1"))
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
        String responseBody = mvc.perform(MockMvcRequestBuilders
                        .delete(bookUrl + "/9999"))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse().getContentAsString(Charset.defaultCharset());
        System.out.println(responseBody);
        ResultDto resultDto = JsonHelperUtils.parseJson(responseBody, ResultDto.class);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.isSuccess()).isFalse();
        assertThat(resultDto.getValue()).isNull();
    }
}