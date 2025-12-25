package ru.dsobin.otus.spring.boot.mapper;

import ru.dsobin.otus.spring.boot.dto.UserDto;
import ru.dsobin.otus.spring.boot.model.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        if (user == null) return null;
        return UserDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }
}
