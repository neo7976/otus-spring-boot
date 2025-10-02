package ru.dsobin.otus.spring.boot.quiz.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
public class QuizConfig {

    @Bean("quizResource")
    public Resource questionsResource(@Value("${quiz.questions.file}") String fileName) {
        return new ClassPathResource(fileName);
    }
}