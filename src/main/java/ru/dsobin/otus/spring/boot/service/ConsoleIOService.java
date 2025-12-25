package ru.dsobin.otus.spring.boot.service;

import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleIOService {

    private final Scanner scanner = new Scanner(System.in);

    public void print(String message) {
        System.out.println(message);
    }

    public String readLine() {
        return scanner.nextLine().trim();
    }
}
