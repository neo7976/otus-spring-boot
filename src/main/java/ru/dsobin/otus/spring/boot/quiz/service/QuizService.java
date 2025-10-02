package ru.dsobin.otus.spring.boot.quiz.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import ru.dsobin.otus.spring.boot.quiz.model.Question;
import ru.dsobin.otus.spring.boot.quiz.reader.QuestionReader;

import java.util.List;
import java.util.Scanner;

@Service
public class QuizService {
    @Value("${quiz.passing.score}")
    private int passingScore;

    private final QuestionReader questionReader;
    private final MessageSource messageSource;

    public QuizService(QuestionReader questionReader, MessageSource messageSource) {
        this.questionReader = questionReader;
        this.messageSource = messageSource;
    }

    public void runQuiz() {
        Scanner scanner = new Scanner(System.in);
        var locale = LocaleContextHolder.getLocale();

        System.out.println(messageSource.getMessage("quiz.enter.first.name", null, locale));
        String firstName = scanner.nextLine().trim();

        System.out.println(messageSource.getMessage("quiz.enter.last.name", null, locale));
        String lastName = scanner.nextLine().trim();

        String welcome = messageSource.getMessage("quiz.welcome", new Object[]{firstName, lastName}, locale);
        System.out.println("\n" + welcome + "\n");

        List<Question> questions = questionReader.readQuestions();
        int totalQuestions = Math.min(5, questions.size());
        int correctAnswers = 0;

        for (int i = 0; i < totalQuestions; i++) {
            Question q = questions.get(i);
            System.out.println(messageSource.getMessage("quiz.question", new Object[]{i + 1}, locale));
            System.out.println(q.getText());

            if (q.isFreeResponse()) {
                System.out.println(messageSource.getMessage("quiz.free.answer", null, locale));
                String answer = scanner.nextLine().trim();
                if (!answer.isEmpty()) {
                    correctAnswers++;
                }
            } else {
                List<String> options = q.getOptions();
                for (int j = 0; j < options.size(); j++) {
                    char letter = (char) ('A' + j);
                    System.out.println("  " + letter + ". " + options.get(j));
                }
                System.out.println(messageSource.getMessage("quiz.multiple.choice", null, locale));
                String input = scanner.nextLine().trim().toUpperCase();

                if (input.length() == 1) {
                    int userIndex = input.charAt(0) - 'A';
                    if (userIndex >= 0 && userIndex < options.size()) {
                        if (userIndex == q.getCorrectAnswerIndex()) {
                            correctAnswers++;
                        }
                    }
                }
            }
            System.out.println();
        }

        System.out.println(messageSource.getMessage("quiz.completed", null, locale));
        System.out.println(messageSource.getMessage("quiz.result", new Object[]{correctAnswers, totalQuestions}, locale));

        if (correctAnswers >= passingScore) {
            System.out.println(messageSource.getMessage("quiz.passed", null, locale));
        } else {
            System.out.println(messageSource.getMessage("quiz.failed", null, locale));
        }
    }
}