package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.repository;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select count(r), coalesce(avg(r.rating), 0.0) from Review r")
    Object[] getReviewStats();
}
