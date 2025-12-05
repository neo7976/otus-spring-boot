package ru.dsobin.otus.spring.boot.quiz.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.dsobin.otus.spring.boot.quiz.model.Question;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuizServiceTest {

    static class FakeIO extends ConsoleIOService {
        private final Deque<String> inputs;
        private final List<String> outputs = new ArrayList<>();
        FakeIO(List<String> inputs) { this.inputs = new ArrayDeque<>(inputs); }
        @Override public void print(String message) { outputs.add(message); }
        @Override public String readLine() { return inputs.isEmpty() ? "" : inputs.pollFirst(); }
        String joinedOutput() { return String.join("\n", outputs); }
        List<String> getOutputs() { return outputs; }
    }

    private List<Question> sampleQuestions() {
        // Two MCQs and one free-response
        Question q1 = new Question("Q1?", List.of("A1","B1","C1"), 1); // correct=B
        Question q2 = new Question("Q2?", List.of("A2","B2"), 0); // correct=A
        Question q3 = new Question("Q3 free", List.of("(free)"), null); // free
        return List.of(q1, q2, q3);
    }

    @Test
    @DisplayName("Порог пройден: 2 из 3 правильных -> passed")
    void shouldPassWhenScoreMeetsThreshold() {
        var questionService = mock(QuestionService.class);
        when(questionService.getQuestions()).thenReturn(sampleQuestions());

        var quizService = new QuizService(questionService);
        ReflectionTestUtils.setField(quizService, "passingScore", 2);

        // Answers: B (correct), C (wrong), any free text (treated as correct)
        var io = new FakeIO(List.of("b", "c", "John"));
        quizService.conductQuiz(io);

        String out = io.joinedOutput();
        assertTrue(out.contains("Quiz finished!"));
        assertTrue(out.contains("Correct answers: 2 out of 3"));
        assertTrue(out.contains("Congratulations"));
    }

    @Test
    @DisplayName("Порог не достигнут: 1 из 3 правильных -> failed")
    void shouldFailWhenScoreBelowThreshold() {
        var questionService = mock(QuestionService.class);
        when(questionService.getQuestions()).thenReturn(sampleQuestions());

        var quizService = new QuizService(questionService);
        ReflectionTestUtils.setField(quizService, "passingScore", 2);

        // Answers: A (wrong), B (wrong for q2 since correct A), empty for free -> empty is incorrect for free
        var io = new FakeIO(List.of("a", "b", ""));
        quizService.conductQuiz(io);

        String out = io.joinedOutput();
        assertTrue(out.contains("Quiz finished!"));
        assertTrue(out.contains("Correct answers: 0 out of 3") || out.contains("Correct answers: 1 out of 3"));
        assertTrue(out.contains("did not pass"));
    }
}
