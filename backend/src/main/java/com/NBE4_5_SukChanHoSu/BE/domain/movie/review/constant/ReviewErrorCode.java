package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.constant;

import lombok.Getter;

@Getter
public enum ReviewErrorCode {
    REVIEW_NOT_FOUND("404", "존재하지 않는 리뷰 입니다.");

    private final String code;
    private final String message;

    ReviewErrorCode(String code, String message) {
        this.code = code.split("-")[0];
        this.message = message;
    }
}
