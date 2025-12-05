package ru.dsobin.otus.spring.boot.quiz.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import ru.dsobin.otus.spring.boot.quiz.model.Question;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class CsvQuestionDaoTest {

//    @Autowired
//    private Resource questionsResource;
//
//    @Test
//    @DisplayName(value = "Проверка теста csv на формат сообщений")
//    void shouldReadQuestionsFromCsv() {
//        List<Question> questions = questionsResource.re;
//
//        assertEquals(5, questions.size());
//
//        Question q1 = questions.get(0);
//        assertFalse(q1.isFreeResponse());
//        assertEquals(0, q1.getCorrectAnswerIndex());
//        assertEquals("Programming language", q1.getOptions().get(0));
//
//        Question q2 = questions.get(4);
//        assertTrue(q2.isFreeResponse());
//    }
}