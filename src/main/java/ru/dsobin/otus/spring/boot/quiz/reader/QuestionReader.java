package ru.dsobin.otus.spring.boot.quiz.reader;

import ru.dsobin.otus.spring.boot.quiz.model.Question;
import java.util.List;

public interface QuestionReader {
    List<Question> readQuestions();
}
