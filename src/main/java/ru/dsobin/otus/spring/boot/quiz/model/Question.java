package ru.dsobin.otus.spring.boot.quiz.model;

import java.util.List;

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

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public boolean isFreeResponse() {
        return isFreeResponse;
    }

    public Integer getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Question: ").append(text).append("\n");
        if (!isFreeResponse) {
            for (int i = 0; i < options.size(); i++) {
                sb.append("  ").append((char) ('A' + i)).append(". ").append(options.get(i)).append("\n");
            }
        } else {
            sb.append("  [Free text answer]\n");
        }
        return sb.toString();
    }
}
