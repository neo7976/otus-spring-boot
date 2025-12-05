package ru.dsobin.otus.spring.boot.quiz.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.dsobin.otus.spring.boot.quiz.model.Question;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class QuestionServiceIntegrationTest {

    @Autowired
    private QuestionService questionService;

    @Test
    @DisplayName("Контекст поднимается с профилем test и возвращает 5 вопросов из questions-test.csv")
    void shouldLoadQuestionsFromTestCsv() {
        List<Question> questions = questionService.getQuestions();
        assertEquals(5, questions.size());

        Question q1 = questions.get(0);
        assertFalse(q1.isFreeResponse());
        assertEquals(0, q1.getCorrectAnswerIndex());
        assertEquals("Programming language", q1.getOptions().get(0));

        Question q5 = questions.get(4);
        assertTrue(q5.isFreeResponse());
        assertNull(q5.getCorrectAnswerIndex());
        assertEquals(List.of("(free)"), q5.getOptions());
    }
}
