package ru.dsobin.otus.spring.boot.quiz.service;

import org.springframework.stereotype.Service;
import ru.dsobin.otus.spring.boot.quiz.dao.QuestionDao;
import ru.dsobin.otus.spring.boot.quiz.model.Question;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    private final QuestionDao questionDao;

    public QuestionService(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    public List<Question> getQuestions() {
        List<String> lines = questionDao.readLines();
        List<Question> questions = new ArrayList<>();

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(",", -1); // -1 чтобы сохранить пустые концы
            if (parts.length < 2) continue; // минимум: текст + индекс или (free)

            String questionText = parts[0].trim();
            String secondField = parts[1].trim();

            // Определяем тип вопроса
            if ("(free)".equalsIgnoreCase(secondField)) {
                // Свободный ответ: нет опций, correctAnswerIndex = null
                questions.add(new Question(questionText, List.of("(free)"), null));
            } else {
                // Обычный вопрос: второй элемент — индекс правильного ответа
                try {
                    int correctIndex = Integer.parseInt(secondField);
                    List<String> options = new ArrayList<>();
                    // Опции начинаются с parts[2]
                    for (int i = 2; i < parts.length; i++) {
                        options.add(parts[i].trim());
                    }

                    // Валидация: индекс в пределах
                    if (correctIndex < 0 || correctIndex >= options.size()) {
                        throw new IllegalArgumentException(
                                "Invalid correct answer index " + correctIndex +
                                        " for question: " + questionText + " (options count: " + options.size() + ")");
                    }

                    questions.add(new Question(questionText, options, correctIndex));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Expected correct answer index or '(free)', got: " + secondField, e);
                }
            }
        }

        return questions;
    }
}