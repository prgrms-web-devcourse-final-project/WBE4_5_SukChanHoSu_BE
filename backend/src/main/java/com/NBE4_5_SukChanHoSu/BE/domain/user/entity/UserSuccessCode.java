package com.NBE4_5_SukChanHoSu.BE.domain.user.entity;

import lombok.Getter;

@Getter
public enum UserSuccessCode {
    JOIN_SUCCESS("200-1", "회원가입이 완료되었습니다."),
    LOGIN_SUCCESS("200-2", "로그인이 완료되었습니다."),
    LOGOUT_SUCCESS("200-3", "로그아웃이 완료되었습니다.");

    private final String code;
    private final String message;

    UserSuccessCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
