package ru.dsobin.otus.spring.boot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import ru.dsobin.otus.spring.boot.quiz.service.QuizService;

@SpringBootApplication
public class OtusSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(OtusSpringBootApplication.class, args);
	}

	@Bean
	@Profile("!test")
	public CommandLineRunner runQuiz(QuizService quizService) {
		return args -> quizService.runQuiz();
	}
}
