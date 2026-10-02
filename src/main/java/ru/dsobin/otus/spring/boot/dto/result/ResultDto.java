package ru.dsobin.otus.spring.boot.dto.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResultDto<T> extends ResultStatusDto {
    private T value;

    public ResultDto(boolean success, String msg, T value) {
        super(success, msg);
        this.value = value;
    }

    public ResultDto(boolean success, T value) {
        super(success);
        this.value = value;
    }
}
