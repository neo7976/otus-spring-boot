package ru.dsobin.otus.spring.boot.quiz.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.dsobin.otus.spring.boot.quiz.model.Question;
import ru.dsobin.otus.spring.boot.quiz.reader.QuestionReader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class CsvQuestionServiceTest {

    @Autowired
    private QuestionReader questionReader;

    @Test
    @DisplayName(value = "Проверка теста csv на формат сообщений")
    void shouldReadQuestionsFromCsv() {
        List<Question> questions = questionReader.readQuestions();

        assertEquals(5, questions.size());

        Question q1 = questions.get(0);
        assertFalse(q1.isFreeResponse());
        assertEquals(0, q1.getCorrectAnswerIndex());
        assertEquals("Programming language", q1.getOptions().get(0));

        Question q2 = questions.get(4);
        assertTrue(q2.isFreeResponse());
    }
}