package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.service;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.responseCode.ReviewErrorCode;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response.ReviewLikeResponseDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.entity.Review;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.entity.ReviewLike;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.repository.ReviewLikeRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.repository.ReviewRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.global.exception.ServiceException;
import com.NBE4_5_SukChanHoSu.BE.global.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class ReviewLikeService {
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final RedissonClient redissonClient;

    private static final String LOCK_ACQUIRE_FAIL_MESSAGE = "Lock 획득 실패: 잠시 후 다시 시도해주세요";
    private static final String LOCK_INTERRUPTED_MESSAGE = "Lock 획득 중 인터럽트가 발생했습니다";

    private static final String LOCK_PREFIX = "lock:review:like:";
    private static final int LIKE_INCREMENT = 1;
    private static final int LIKE_DECREMENT = -1;

    @Transactional
    public ReviewLikeResponseDto addLike(Long reviewId) {
        User user = SecurityUtil.getCurrentUser();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ServiceException(
                        ReviewErrorCode.REVIEW_NOT_FOUND.getCode(),
                        ReviewErrorCode.REVIEW_NOT_FOUND.getMessage()
                ));

        /*
          좋아요 기능 구현
          게시물 좋아요 처리를 위해 분산 락 획득
         */
        String lockKey = LOCK_PREFIX + reviewId;
        RLock lock = redissonClient.getLock(lockKey);

        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new RuntimeException(LOCK_ACQUIRE_FAIL_MESSAGE);
            }

            ReviewLike reviewLike = reviewLikeRepository.findById(reviewId)
                    .orElse(null);

            if (reviewLike == null) {
                reviewLike = ReviewLike.builder()
                        .user(user)
                        .review(review)
                        .build();
                reviewLikeRepository.save(reviewLike);
                review.setLikeCount(review.getLikeCount() + LIKE_INCREMENT);
                return new ReviewLikeResponseDto(reviewId, user.getUserProfile().getNickName(), true);
            } else {
                reviewLikeRepository.delete(reviewLike);
                review.setLikeCount(review.getLikeCount() + LIKE_DECREMENT);
                return new ReviewLikeResponseDto(reviewId, user.getUserProfile().getNickName(), false);

            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(LOCK_INTERRUPTED_MESSAGE);
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
