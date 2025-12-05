package ru.dsobin.otus.spring.boot.quiz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Profile("!test")
public class QuizRunner implements CommandLineRunner {

    private final QuizService quizService;
    private final ConsoleIOService io;

    @Override
    public void run(String... args) {
        quizService.conductQuiz(io);
    }
}
