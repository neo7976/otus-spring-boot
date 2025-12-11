package ru.dsobin.otus.spring.boot.utils;


import org.springframework.data.domain.Page;
import ru.dsobin.otus.spring.boot.dto.result.PageDataDto;
import java.util.List;

public class PageDataUtil {

    public static <D> PageDataDto<D> getData(Page<?> page, List<D> elements) {
        PageDataDto<D> pageDataDto = new PageDataDto<>();

        return pageDataDto.setData(elements)
                .setTotalCount(page.getTotalElements())
                .setCountPage(page.getTotalPages())
                .setStatus(200);
    }
}
