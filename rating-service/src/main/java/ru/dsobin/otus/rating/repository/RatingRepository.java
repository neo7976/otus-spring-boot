package ru.dsobin.otus.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.dsobin.otus.rating.model.Rating;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByBookIdAndUsername(Long bookId, String username);

    long countByBookId(Long bookId);

    @Query("select avg(r.score) from Rating r where r.bookId = :bookId")
    Double findAverageScore(@Param("bookId") Long bookId);
}
