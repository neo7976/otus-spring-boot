package ru.dsobin.otus.spring.boot.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.dsobin.otus.spring.boot.dto.result.PageInfoDto;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookPageInfoDto extends PageInfoDto {
    private Long authorId;
    private Long genreId;
    private Long bookId;
    private String title;

    public BookPageInfoDto(String sortBy, String orderBy, int pageNumber, int pageSize,
                           Long authorId,
                           Long genreId,
                           Long bookId,
                           String title) {
        super(sortBy, orderBy, pageNumber, pageSize);
        this.bookId = bookId;
        this.genreId = genreId;
        this.authorId = authorId;
        this.title = title;

    }

    private <T extends Enum<T>> Set<T> parseEnumSet(String enumStr, Class<T> enumClass) {
        Set<T> set = new HashSet<>();

        if (enumStr == null || enumStr.isEmpty() || "all".equalsIgnoreCase(enumStr)) {
            set.addAll(Arrays.asList(enumClass.getEnumConstants()));
            return set;
        }

        if (enumStr.contains(",")) {
            String[] split = enumStr.split(",");
            for (String s : split) {
                set.add(Enum.valueOf(enumClass, s.trim()));
            }
        } else {
            set.add(Enum.valueOf(enumClass, enumStr.trim()));
        }
        return set;
    }
}
