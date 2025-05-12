package com.NBE4_5_SukChanHoSu.BE.domain.movie.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.constant.ReviewSuccessCode;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.controller.ReviewController;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.request.ReviewRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response.ReviewResponseDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.service.ReviewService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
public class ReviewControllerTest {

    @InjectMocks
    private ReviewController reviewController;

    @Mock
    private ReviewService reviewService;

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReviewSuccess() {
        // given
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setTitle("아바타");
        requestDto.setContent("Amazing movie!");
        requestDto.setRating(5.0);

        ReviewResponseDto reviewResponse = new ReviewResponseDto();
        reviewResponse.setId(1L);
        reviewResponse.setTitle("아바타");
        reviewResponse.setContent("Amazing movie!");
        reviewResponse.setRating(5.0);

        when(reviewService.createReviewPost(requestDto)).thenReturn(reviewResponse);

        // when
        RsData<ReviewResponseDto> response = reviewController.createReviewPost(requestDto);

        // then
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(ReviewSuccessCode.REVIEW_CREATED.getCode(), response.getCode()),
                () -> assertEquals(ReviewSuccessCode.REVIEW_CREATED.getMessage(), response.getMessage()),
                () -> assertNotNull(response.getData()),
                () -> assertEquals("아바타", response.getData().getTitle())
        );

        verify(reviewService, times(1)).createReviewPost(requestDto);
    }

    @Test
    @DisplayName("리뷰 조회 성공")
    void getReviewByIdSuccess() {
        // given
        Long reviewId = 1L;
        ReviewResponseDto reviewResponse = new ReviewResponseDto();
        reviewResponse.setId(1L);
        reviewResponse.setTitle("아바타");
        reviewResponse.setContent("Amazing movie!");
        reviewResponse.setRating(5.0);

        when(reviewService.getOneReview(reviewId)).thenReturn(reviewResponse);

        // when
        RsData<ReviewResponseDto> response = reviewController.getReviewById(reviewId);

        // then
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(ReviewSuccessCode.REVIEW_FETCHED.getCode(), response.getCode()),
                () -> assertEquals(ReviewSuccessCode.REVIEW_FETCHED.getMessage(), response.getMessage()),
                () -> assertNotNull(response.getData()),
                () -> assertEquals("아바타", response.getData().getTitle())
        );

        verify(reviewService, times(1)).getOneReview(reviewId);
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReviewSuccess() {
        // given
        Long reviewId = 1L;
        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setTitle("아바타");
        requestDto.setContent("Updated review text");
        requestDto.setRating(4.0);

        doNothing().when(reviewService).updateReview(reviewId, requestDto);

        // when
        RsData<?> response = reviewController.updateReview(reviewId, requestDto);

        // then
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(ReviewSuccessCode.REVIEW_UPDATED.getCode(), response.getCode()),
                () -> assertEquals(ReviewSuccessCode.REVIEW_UPDATED.getMessage(), response.getMessage())
        );

        verify(reviewService, times(1)).updateReview(reviewId, requestDto);
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReviewSuccess() {
        // given
        Long reviewId = 1L;
        doNothing().when(reviewService).deleteReview(reviewId);

        // when
        RsData<?> response = reviewController.deleteReview(reviewId);

        // then
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(ReviewSuccessCode.REVIEW_DELETED.getCode(), response.getCode()),
                () -> assertEquals(ReviewSuccessCode.REVIEW_DELETED.getMessage(), response.getMessage())
        );

        verify(reviewService, times(1)).deleteReview(reviewId);
    }
}
