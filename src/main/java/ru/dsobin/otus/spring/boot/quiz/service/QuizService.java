package ru.dsobin.otus.spring.boot.quiz.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import ru.dsobin.otus.spring.boot.quiz.model.Question;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final MessageSource messageSource;
    private final QuestionService questionService;
    @Value("${quiz.passing.score}")
    private int passingScore;

    public void conductQuiz(ConsoleIOService io) {
        List<Question> questions = questionService.getQuestions();
        int correctAnswers = 0;

        var locale = LocaleContextHolder.getLocale();

        io.print(messageSource.getMessage("quiz.welcome.simple", null, locale));
        io.print(messageSource.getMessage("quiz.instructions", null, locale) + "\n");

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            io.print(messageSource.getMessage("quiz.question", new Object[]{i + 1}, locale));
            io.print(q.getText());

            if (!q.isFreeResponse()) {
                // Print options A, B, C...
                for (int j = 0; j < q.getOptions().size(); j++) {
                    char optionLetter = (char) ('A' + j);
                    io.print("  " + optionLetter + ". " + q.getOptions().get(j));
                }
                io.print(messageSource.getMessage("quiz.multiple.choice", null, locale));
            } else {
                io.print(messageSource.getMessage("quiz.free.answer", null, locale));
            }

            String userAnswer = io.readLine().toUpperCase();

            if (isAnswerCorrect(q, userAnswer)) {
                correctAnswers++;
                io.print(messageSource.getMessage("quiz.correct", null, locale) + "\n");
            } else {
                io.print(messageSource.getMessage("quiz.incorrect", null, locale) + "\n");
            }
        }

        boolean passed = correctAnswers >= passingScore;
        io.print(messageSource.getMessage("quiz.completed", null,locale));
        io.print(messageSource.getMessage("quiz.result", new Object[]{correctAnswers, questions.size()}, locale));
        io.print(passed ? messageSource.getMessage("quiz.passed", null,locale) : messageSource.getMessage("quiz.failed", null,locale));
    }

    private boolean isAnswerCorrect(Question question, String userAnswer) {
        if (question.isFreeResponse()) {
            return !userAnswer.isEmpty() && !"(FREE)".equals(userAnswer);
        } else {
            if (userAnswer.length() != 1) {
                return false;
            }
            char c = userAnswer.charAt(0);
            if (c < 'A' || c > 'Z') {
                return false;
            }
            int selectedOptionIndex = c - 'A';

            if (selectedOptionIndex < 0 || selectedOptionIndex >= question.getOptions().size()) {
                return false;
            }

            return selectedOptionIndex == question.getCorrectAnswerIndex();
        }
    }
}