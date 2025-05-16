package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.repository;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    ReviewLike findByReviewIdAndUserId(Long reviewId, Long userId);
}
