package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.service;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.responseCode.ReviewErrorCode;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response.ReviewLikeResponseDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.entity.Review;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.entity.ReviewLike;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.repository.ReviewLikeRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.repository.ReviewRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.global.exception.ServiceException;
import com.NBE4_5_SukChanHoSu.BE.global.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewLikeTest {

    private ReviewLikeService reviewLikeService;
    private ReviewLikeRepository reviewLikeRepository;
    private ReviewRepository reviewRepository;
    private RedissonClient redissonClient;
    private RLock rLock;

    @BeforeEach
    void setUp() {
        reviewLikeRepository = mock(ReviewLikeRepository.class);
        reviewRepository = mock(ReviewRepository.class);
        redissonClient = mock(RedissonClient.class);
        rLock = mock(RLock.class);

        reviewLikeService = new ReviewLikeService(reviewLikeRepository, reviewRepository, redissonClient);
    }

    @Test
    @DisplayName("리뷰 좋아요 - 성공적으로 좋아요 추가")
    void addLikeSuccessFirstTime() throws InterruptedException {
        Long reviewId = 1L;

        User mockUser = createMockUser("닉네임");
        Review review = new Review();
        review.setId(reviewId);
        review.setLikeCount(0);

        try (MockedStatic<SecurityUtil> securityUtilMockedStatic = mockStatic(SecurityUtil.class)) {
            securityUtilMockedStatic.when(SecurityUtil::getCurrentUser).thenReturn(mockUser);

            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
            when(redissonClient.getLock("lock:review:like:" + reviewId)).thenReturn(rLock);
            when(rLock.tryLock(5, 10, TimeUnit.SECONDS)).thenReturn(true);
            when(reviewLikeRepository.findById(reviewId)).thenReturn(Optional.empty());

            ReviewLikeResponseDto result = reviewLikeService.addLike(reviewId);

            assertTrue(result.isLiked());
            assertEquals(1, review.getLikeCount());
            assertEquals("닉네임", result.getUsername());
            verify(reviewLikeRepository, times(1)).save(any());
        }
    }

    @Test
    @DisplayName("리뷰 좋아요 - 이미 눌렀던 좋아요를 취소")
    void removeLikeSuccess() throws InterruptedException {
        Long reviewId = 2L;
        User mockUser = createMockUser("닉네임");
        Review review = new Review();
        review.setId(reviewId);
        review.setLikeCount(1);
        ReviewLike existingLike = ReviewLike.builder().review(review).user(mockUser).build();

        try (MockedStatic<SecurityUtil> securityUtilMockedStatic = mockStatic(SecurityUtil.class)) {
            securityUtilMockedStatic.when(SecurityUtil::getCurrentUser).thenReturn(mockUser);

            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
            when(redissonClient.getLock("lock:review:like:" + reviewId)).thenReturn(rLock);
            when(rLock.tryLock(5, 10, TimeUnit.SECONDS)).thenReturn(true);
            when(reviewLikeRepository.findById(reviewId)).thenReturn(Optional.of(existingLike));

            ReviewLikeResponseDto result = reviewLikeService.addLike(reviewId);

            assertFalse(result.isLiked());
            assertEquals(0, review.getLikeCount());
            verify(reviewLikeRepository, times(1)).delete(existingLike);
        }
    }

    @Test
    @DisplayName("리뷰 좋아요 - 리뷰가 존재하지 않을 때 예외 발생")
    void addLikeReviewNotFound() {
        Long reviewId = 3L;

        try (MockedStatic<SecurityUtil> securityUtilMockedStatic = mockStatic(SecurityUtil.class)) {
            securityUtilMockedStatic.when(SecurityUtil::getCurrentUser).thenReturn(createMockUser("닉네임"));

            when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

            ServiceException exception = assertThrows(ServiceException.class, () ->
                    reviewLikeService.addLike(reviewId));

            assertEquals(ReviewErrorCode.REVIEW_NOT_FOUND.getCode(), exception.getCode());
        }
    }

    private User createMockUser(String nickname) {
        UserProfile profile = UserProfile.builder().nickName(nickname).build();
        return User.builder().userProfile(profile).build();
    }
}
