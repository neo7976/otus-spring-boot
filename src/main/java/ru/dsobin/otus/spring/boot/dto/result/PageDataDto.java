package ru.dsobin.otus.spring.boot.dto.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class PageDataDto<T> {
    List<T> data;
    int countPage;
    long totalCount;
    int status;

    public PageDataDto<T> setData(List<T> data) {
        this.data = data;
        return this;
    }

    public PageDataDto<T> setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public PageDataDto<T> setCountPage(int countPage) {
        this.countPage = countPage;
        return this;
    }

    public PageDataDto<T> setStatus(int status) {
        this.status = status;
        return this;
    }
}