package ru.dsobin.otus.spring.boot.quiz.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.dsobin.otus.spring.boot.quiz.dao.QuestionDao;
import ru.dsobin.otus.spring.boot.quiz.model.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionServiceTest {

    static class InMemoryQuestionDao implements QuestionDao {
        private final List<String> lines;
        InMemoryQuestionDao(List<String> lines) { this.lines = lines; }
        @Override public List<String> readLines() { return lines; }
    }

    @Test
    @DisplayName("Парсинг CSV в объекты Question: 4 теста с вариантами + 1 свободный")
    void shouldParseQuestionsFromCsvLines() {
        List<String> csv = Arrays.asList(
                "What is Java?,0,Programming language,Scripting language,Markup language",
                "What does JVM stand for?,1,Java Variable Machine,Java Virtual Machine,Just Very Magic",
                "Is Spring a framework?,0,Yes,No,Maybe",
                "What is IoC?,2,Input of Code,Interface over Class,Inversion of Control",
                "Free answer: What is your name?,(free)"
        );
        QuestionService service = new QuestionService(new InMemoryQuestionDao(csv));

        List<Question> questions = service.getQuestions();
        assertEquals(5, questions.size());

        Question q1 = questions.get(0);
        assertFalse(q1.isFreeResponse());
        assertEquals(0, q1.getCorrectAnswerIndex());
        assertEquals(3, q1.getOptions().size());
        assertEquals("Programming language", q1.getOptions().get(0));

        Question q4 = questions.get(4);
        assertTrue(q4.isFreeResponse());
        assertNull(q4.getCorrectAnswerIndex());
        assertEquals(List.of("(free)"), q4.getOptions());
    }

    @Test
    @DisplayName("Неверный индекс правильного ответа -> IllegalArgumentException")
    void shouldThrowOnInvalidCorrectIndex() {
        List<String> csv = List.of("Q?,3,A,B"); // options size=1 (since from index 2 only one), index=3 invalid
        QuestionService service = new QuestionService(new InMemoryQuestionDao(csv));
        assertThrows(IllegalArgumentException.class, service::getQuestions);
    }

    @Test
    @DisplayName("Второе поле neither number nor (free) -> IllegalArgumentException")
    void shouldThrowOnInvalidSecondField() {
        List<String> csv = List.of("Q?,oops,A,B");
        QuestionService service = new QuestionService(new InMemoryQuestionDao(csv));
        assertThrows(IllegalArgumentException.class, service::getQuestions);
    }
}
