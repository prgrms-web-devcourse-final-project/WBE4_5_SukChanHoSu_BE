package com.NBE4_5_SukChanHoSu.BE.domain.user.entity;

import lombok.Getter;

@Getter
public enum UserErrorCode {
    PASSWORDS_NOT_MATCH("400-1", "비밀번호가 일치하지 않습니다."),
    EMAIL_ALREADY_EXISTS("400-2", "이미 존재하는 이메일입니다."),

    LOGOUT_UNAUTHORIZED("401-1", "인증되지 않은 사용자로 로그아웃할 수 없습니다."),
    USER_UNAUTHORIZED("401-2", "인증되지 않은 사용자입니다."),
    INVALID_REFRESH_TOKEN("401-3", "유효하지 않은 RefreshToken입니다."),
    BLACKLISTED_REFRESH_TOKEN("401-4", "로그아웃된 RefreshToken입니다."),
    EMAIL_NOT_VERIFY("401-5", "이메일 인증을 완료해주세요."),

    EMAIL_NOT_FOUND("404-1", "존재하지 않는 이메일입니다."),
    USER_NOT_FOUND("404-2", "존재하지 않는 사용자입니다.");

    private final String code;
    private final String message;

    UserErrorCode(String code, String message) {
        this.code = code.split("-")[0];
        this.message = message;
    }
}
