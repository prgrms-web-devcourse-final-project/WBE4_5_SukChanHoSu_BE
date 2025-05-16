package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.request.ReviewCreateDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.request.ReviewUpdateDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response.AllReviewDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response.ReviewResponseDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.responseCode.ReviewSuccessCode;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.service.ReviewService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movie/review")
@Tag(name = "영화 리뷰", description = "영화 리뷰 작성, 조회, 수정, 삭제 API")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @Operation(
            summary = "리뷰 작성",
            description = "영화에 대한 리뷰를 작성합니다."
    )
    public RsData<ReviewResponseDto> createReviewPost(@RequestBody ReviewCreateDto requestDto) {
        ReviewResponseDto reviewResponseDto = reviewService.createReviewPost(requestDto);
        return new RsData<>(
                ReviewSuccessCode.REVIEW_CREATED.getCode(),
                ReviewSuccessCode.REVIEW_CREATED.getMessage(),
                reviewResponseDto
        );
    }

    @GetMapping("/{reviewId}")
    @Operation(
            summary = "단건 리뷰 조회",
            description = "리뷰 ID를 사용하여 특정 리뷰를 조회합니다."
    )
    public RsData<ReviewResponseDto> getReviewById(@PathVariable Long reviewId) {
        ReviewResponseDto reviewResponseDto = reviewService.getOneReview(reviewId);
        return new RsData<>(
                ReviewSuccessCode.REVIEW_FETCHED.getCode(),
                ReviewSuccessCode.REVIEW_FETCHED.getMessage(),
                reviewResponseDto
        );
    }

    @GetMapping
    @Operation(
            summary = "영화 리뷰 목록 조회",
            description = "영화 제목을 기준으로 리뷰 목록을 조회합니다. 정렬 기능을 선택적으로 적용할 수 있습니다."
    )
    public RsData<AllReviewDto> getAllReviews(
            @RequestParam Long movieId,
            @RequestParam(required = false) String sort) {
        AllReviewDto allReviewDto = reviewService.getAllReviewsByMovieId(movieId, sort);
        return new RsData<>(
                ReviewSuccessCode.REVIEW_LIST_FETCHED.getCode(),
                ReviewSuccessCode.REVIEW_LIST_FETCHED.getMessage(),
                allReviewDto
        );
    }

    @PatchMapping("/{reviewId}")
    @Operation(
            summary = "리뷰 수정",
            description = "리뷰 ID를 사용하여 기존 리뷰를 수정합니다."
    )
    public RsData<ReviewResponseDto> updateReview(@PathVariable Long reviewId, @RequestBody ReviewUpdateDto requestDto) {
        reviewService.updateReview(reviewId, requestDto);
        ReviewResponseDto reviewResponseDto = reviewService.getOneReview(reviewId);
        return new RsData<>(
                ReviewSuccessCode.REVIEW_UPDATED.getCode(),
                ReviewSuccessCode.REVIEW_UPDATED.getMessage(),
                reviewResponseDto
        );
    }

    @DeleteMapping("/{reviewId}")
    @Operation(
            summary = "리뷰 삭제",
            description = "리뷰 ID를 사용하여 특정 리뷰를 삭제합니다."
    )
    public RsData<?> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return new RsData<>(
                ReviewSuccessCode.REVIEW_DELETED.getCode(),
                ReviewSuccessCode.REVIEW_DELETED.getMessage()
        );
    }
}