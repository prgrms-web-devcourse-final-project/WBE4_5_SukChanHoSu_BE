package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.constant.ReviewSuccessCode;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response.ReviewLikeResponseDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.service.ReviewLikeService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/review/like")
@RequiredArgsConstructor
@Tag(name = "영화 리뷰 게시판 좋아요 기능", description = "좋아요 추가/취소 기능")
public class ReviewLikeController {
    private final ReviewLikeService reviewLikeService;

    @PostMapping("/{reviewId}")
    @Operation(
            summary = "리뷰 게시판 좋아요 추가/취소",
            description = "현재 사용자가 리뷰에 대한 좋아요 추가/취소 합니다."
    )
    public RsData<ReviewLikeResponseDto> reviewLike(@PathVariable Long reviewId) {
        ReviewLikeResponseDto reviewLike = reviewLikeService.addLike(reviewId);

        ReviewSuccessCode code = reviewLike.isLiked()
                ? ReviewSuccessCode.REVIEW_LIKE_ADD
                : ReviewSuccessCode.REVIEW_LIKE_CANCEL;

        return new RsData<>(
                code.getCode(),
                code.getMessage(),
                reviewLike
        );
    }
}
