package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AllReviewDto {
    private List<ReviewResponseDto> reviews;
    private int totalReviews;
    private Double totalRating;

    public AllReviewDto(List<ReviewResponseDto> reviewList, Object totalReviews, Object totalRating) {
        this.reviews = reviewList;
        this.totalReviews = (int) totalReviews;
        this.totalRating = (Double) totalRating;
    }
}
