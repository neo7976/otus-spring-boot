package ru.dsobin.otus.spring.boot.quiz.model;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public class Question {
    private final String text;
    private final List<String> options;
    private final boolean isFreeResponse;
    private final Integer correctAnswerIndex;

    public Question(String text, List<String> options, Integer correctAnswerIndex) {
        this.text = text;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.isFreeResponse = correctAnswerIndex == null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Question: ").append(text).append("\n");
        if (isFreeResponse) {
            sb.append("  [Free text answer]\n");
        } else {
            for (int i = 0; i < options.size(); i++) {
                sb.append("  ").append((char) ('A' + i)).append(". ").append(options.get(i)).append("\n");
            }
            sb.append("  (Correct: ").append((char) ('A' + correctAnswerIndex)).append(")\n");
        }
        return sb.toString();
    }
}