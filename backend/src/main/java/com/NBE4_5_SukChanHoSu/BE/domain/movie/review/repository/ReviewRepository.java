package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.repository;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
