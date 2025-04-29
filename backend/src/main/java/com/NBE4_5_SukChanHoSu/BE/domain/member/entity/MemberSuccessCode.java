package com.NBE4_5_SukChanHoSu.BE.domain.member.entity;

import lombok.Getter;

@Getter
public enum MemberSuccessCode {
    JOIN_SUCCESS("200-1", "회원가입이 완료되었습니다."),
    LOGIN_SUCCESS("200-2", "로그인이 완료되었습니다.");

    private final String code;
    private final String message;

    MemberSuccessCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
