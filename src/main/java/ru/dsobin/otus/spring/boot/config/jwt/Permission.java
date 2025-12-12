package ru.dsobin.otus.spring.boot.config.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Permission implements GrantedAuthority {
    ADMIN("Админ"),
    ROLE_ADMIN("Админ"),
    ROLE_USER("Пользователь"),
    USER("Обычный пользователь");

    private final String desc;
    @Override
    public String getAuthority() {
        return this.name();
    }
}
