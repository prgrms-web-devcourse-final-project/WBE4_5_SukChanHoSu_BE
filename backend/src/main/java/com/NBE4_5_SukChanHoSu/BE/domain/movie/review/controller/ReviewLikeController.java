package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.constant.ReviewSuccessCode;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response.ReviewLikeResponseDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.service.ReviewLikeService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/review/like")
@RequiredArgsConstructor
public class ReviewLikeController {
    private final ReviewLikeService reviewLikeService;

    @PostMapping("/{reviewId}")
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
