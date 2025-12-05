package ru.dsobin.otus.spring.boot.quiz.service;

import ru.dsobin.otus.spring.boot.quiz.model.Question;

import java.util.List;

public class ConsoleOutputService {

    public void printQuestions(List<Question> questions) {
        System.out.println("Quiz Questions:\n");
        for (Question q : questions) {
            System.out.println(q);
        }
    }
}
