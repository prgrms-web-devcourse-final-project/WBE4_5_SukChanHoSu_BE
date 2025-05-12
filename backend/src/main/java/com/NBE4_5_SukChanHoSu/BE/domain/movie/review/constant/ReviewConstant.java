package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.constant;

public class ReviewConstant {
    private ReviewConstant() {
    }

    public static final int MIN_RATING = 0;
    public static final int MAX_RATING = 5;

    public static final String MIN_RATING_MESSAGE = "평점은 최소 0점 이상이어야 합니다.";
    public static final String MAX_RATING_MESSAGE = "평점은 최대 5점까지만 입력할 수 있습니다.";
}