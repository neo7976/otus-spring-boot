package ru.dsobin.otus.spring.boot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN_MAIN("Админ"),
    GUEST( "Гость"),
    LEADER( "Лидер Команды"),
    MEMBER(  "Участник"),
    ADMINISTRATOR(  "Администратор проекта"),
    MANAGER( "Менеджер проекта"),

    BACKEND(  "Backend"),
    FRONTEND("Frontend" ),
    DESIGN( "Design"),
    PROJECT_MANAGER("Project manager");

    private final String name;
}

