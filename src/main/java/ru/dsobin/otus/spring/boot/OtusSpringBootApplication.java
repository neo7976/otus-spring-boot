package ru.dsobin.otus.spring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OtusSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(OtusSpringBootApplication.class, args);
	}

}
