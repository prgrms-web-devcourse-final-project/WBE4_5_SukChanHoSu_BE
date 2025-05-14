package com.NBE4_5_SukChanHoSu.BE.domain.movie.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.constant.ReviewSuccessCode;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.controller.ReviewController;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.request.ReviewRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response.AllReviewDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response.ReviewResponseDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.service.ReviewService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

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

    @Test
    @DisplayName("리뷰 목록 조회 - 최신순 정렬")
    void getAllReviewsSortedByCreatedDateDesc() {
        // given
        String movieTitle = "아바타";
        String sort = ""; // 최신순 정렬은 sort가 null 또는 빈 문자열일 때

        ReviewResponseDto review1 = new ReviewResponseDto();
        review1.setId(1L);
        review1.setTitle(movieTitle);
        review1.setContent("리뷰 1");
        review1.setUserName("사용자1");
        review1.setLikeCount(10);
        review1.setRating(4.5);

        ReviewResponseDto review2 = new ReviewResponseDto();
        review2.setId(2L);
        review2.setTitle(movieTitle);
        review2.setContent("리뷰 2");
        review2.setUserName("사용자2");
        review2.setLikeCount(3);
        review2.setRating(3.0);

        List<ReviewResponseDto> reviewList = List.of(review1, review2);
        Long totalReviews = 2L;
        Double totalRating = 3.75;

        AllReviewDto allReviewDto = new AllReviewDto(reviewList, totalReviews, totalRating);

        when(reviewService.getAllReviewsByTitle(movieTitle, sort)).thenReturn(allReviewDto);

        // when
        RsData<AllReviewDto> response = reviewController.getAllReviews(movieTitle, sort);

        // then
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(ReviewSuccessCode.REVIEW_LIST_FETCHED.getCode(), response.getCode()),
                () -> assertEquals(ReviewSuccessCode.REVIEW_LIST_FETCHED.getMessage(), response.getMessage()),
                () -> assertEquals(2, response.getData().getReviews().size()),
                () -> assertEquals("리뷰 1", response.getData().getReviews().get(0).getContent()),
                () -> assertEquals(3.8, response.getData().getTotalRating()) // 반올림 확인
        );

        verify(reviewService, times(1)).getAllReviewsByTitle(movieTitle, sort);
    }

    @Test
    @DisplayName("리뷰 목록 조회 - 좋아요순 정렬")
    void getReviewsSortedByLikeSuccess() {
        // given
        String movieTitle = "인셉션";
        String sort = "like";

        ReviewResponseDto review1 = new ReviewResponseDto();
        review1.setId(1L);
        review1.setTitle("인셉션");
        review1.setContent("정말 좋았어요");
        review1.setUserName("유저1");
        review1.setLikeCount(10);
        review1.setRating(4.5);

        ReviewResponseDto review2 = new ReviewResponseDto();
        review2.setId(2L);
        review2.setTitle("인셉션");
        review2.setContent("최고의 영화입니다");
        review2.setUserName("유저2");
        review2.setLikeCount(8);
        review2.setRating(4.0);

        List<ReviewResponseDto> sortedReviews = List.of(review1, review2);
        Long totalReviews = 2L;
        Double totalRating = 4.25;

        AllReviewDto allReviewDto = new AllReviewDto(sortedReviews, totalReviews, totalRating);

        when(reviewService.getAllReviewsByTitle(movieTitle, sort)).thenReturn(allReviewDto);

        // when
        RsData<AllReviewDto> response = reviewController.getAllReviews(movieTitle, sort);

        // then
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(ReviewSuccessCode.REVIEW_LIST_FETCHED.getCode(), response.getCode()),
                () -> assertEquals(ReviewSuccessCode.REVIEW_LIST_FETCHED.getMessage(), response.getMessage()),
                () -> assertEquals(2, response.getData().getReviews().size()),
                () -> assertEquals("정말 좋았어요", response.getData().getReviews().get(0).getContent()),
                () -> assertEquals(10, response.getData().getReviews().get(0).getLikeCount()),
                () -> assertEquals("최고의 영화입니다", response.getData().getReviews().get(1).getContent()),
                () -> assertEquals(8, response.getData().getReviews().get(1).getLikeCount())
        );

        verify(reviewService, times(1)).getAllReviewsByTitle(movieTitle, sort);
    }


}
