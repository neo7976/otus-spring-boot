package ru.dsobin.otus.spring.boot.dto.result;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@NoArgsConstructor
@Data
public abstract class PageInfoDto implements InfoDto {
    private String sortBy;

    private String orderBy;

    private int pageNumber;

    private int pageSize;

    protected PageInfoDto(String sortBy, String orderBy, int pageNumber, int pageSize) {
        this.sortBy = sortBy;
        this.orderBy = orderBy;
        this.pageNumber = pageNumber == 0 ? 0 : pageNumber - 1;//Фронт отправляет страницы с 1
        this.pageSize = pageSize;
    }

    public Pageable toPageable() {
        Sort sort = Sort.by(sortBy);
        if ("desc".equalsIgnoreCase(orderBy)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
