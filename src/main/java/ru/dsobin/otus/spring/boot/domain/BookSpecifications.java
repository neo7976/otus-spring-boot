package ru.dsobin.otus.spring.boot.domain;

import org.springframework.data.jpa.domain.Specification;
import ru.dsobin.otus.spring.boot.filter.BookPageInfoDto;
import ru.dsobin.otus.spring.boot.model.Author;
import ru.dsobin.otus.spring.boot.model.Book;
import ru.dsobin.otus.spring.boot.model.Genre;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class BookSpecifications {

    public static Specification<Book> withFilter(BookPageInfoDto filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getAuthorId() != null) {
                Join<Book, Author> authorJoin = root.join("author", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(
                        authorJoin.get("id"),
                        filter.getAuthorId()
                ));
            }

            if (filter.getGenreId() != null) {
                Join<Book, Genre> genreJoin = root.join("genre", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(
                        genreJoin.get("id"),
                        filter.getGenreId()
                ));
            }

            if (filter.getBookId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), filter.getBookId()));
            }

            if (filter.getTitle() != null && !filter.getTitle().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + filter.getTitle().toLowerCase() + "%"
                ));
            }

            return predicates.isEmpty()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
