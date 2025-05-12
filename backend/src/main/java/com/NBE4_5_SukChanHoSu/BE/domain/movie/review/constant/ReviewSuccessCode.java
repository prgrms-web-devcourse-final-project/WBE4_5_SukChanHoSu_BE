package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.constant;

import lombok.Getter;

@Getter
public enum ReviewSuccessCode {

    REVIEW_CREATED("200-1", "리뷰 작성 성공"),
    REVIEW_FETCHED("200-2", "리뷰 조회 성공"),
    REVIEW_LIST_FETCHED("200-3", "리뷰 목록 조회 성공"),
    REVIEW_UPDATED("200-4", "리뷰 수정 성공"),
    REVIEW_DELETED("200-5", "리뷰 삭제 성공");

    private final String code;
    private final String message;

    ReviewSuccessCode(String code, String message) {
        this.code = code.split("-")[0];
        this.message = message;
    }
}