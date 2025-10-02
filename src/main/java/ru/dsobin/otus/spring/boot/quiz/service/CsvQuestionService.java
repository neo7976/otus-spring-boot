package ru.dsobin.otus.spring.boot.quiz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.dsobin.otus.spring.boot.quiz.model.Question;
import ru.dsobin.otus.spring.boot.quiz.reader.QuestionReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvQuestionService implements QuestionReader {

    private final Resource questionsResource;

    public CsvQuestionService(Resource questionsResource) {
        this.questionsResource = questionsResource;
    }

    @Override
    public List<Question> readQuestions() {
        List<Question> questions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(questionsResource.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length < 2) {
                    throw new IllegalArgumentException("Invalid format at line " + lineNumber + ": expected at least 2 fields");
                }

                String questionText = parts[0].trim();
                String secondField = parts[1].trim();

                if ("(free)".equals(secondField)) {
                    // Free response: no options beyond marker
                    questions.add(new Question(questionText, List.of("(free)"), null));
                } else {
                    try {
                        int correctIndex = Integer.parseInt(secondField);
                        List<String> options = new ArrayList<>();
                        // Опции начинаются с parts[2]
                        for (int i = 2; i < parts.length; i++) {
                            options.add(parts[i].trim());
                        }
                        if (options.isEmpty()) {
                            throw new IllegalArgumentException("No answer options provided at line " + lineNumber);
                        }
                        if (correctIndex < 0 || correctIndex >= options.size()) {
                            throw new IllegalArgumentException(
                                    "Correct answer index " + correctIndex + " is out of bounds [0, " + (options.size() - 1) + "] at line " + lineNumber);
                        }
                        questions.add(new Question(questionText, options, correctIndex));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(
                                "Expected integer or '(free)' as second field at line " + lineNumber + ", got: " + secondField, e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read questions from CSV", e);
        }
        return questions;
    }
}