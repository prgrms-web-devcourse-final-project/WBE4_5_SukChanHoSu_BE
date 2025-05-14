package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response;

import lombok.Data;

@Data
public class ReviewLikeResponseDto {
    private final Long reviewId;
    private final String username;
    private final boolean liked;

    public ReviewLikeResponseDto(Long reviewId, String username, boolean liked) {
        this.reviewId = reviewId;
        this.username = username;
        this.liked = liked;
    }
}
