package ru.dsobin.otus.spring.boot.utils;

import ru.dsobin.otus.spring.boot.dto.result.ResultDto;

public class ResultUtil {
    public static <D> ResultDto<D> createSuccess(D element, String msg) {
        return new ResultDto<>(true, msg, element);
    }

    public static <D> ResultDto<D> createSuccess(D element) {
        return createSuccess(element, null);
    }
}
