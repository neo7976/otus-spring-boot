package ru.dsobin.otus.spring.boot.dto.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultStatusDto {
    boolean success;
    String msg;

    public ResultStatusDto(boolean success) {
        this.success = success;
    }
}
