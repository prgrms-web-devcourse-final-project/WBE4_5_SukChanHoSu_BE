package com.NBE4_5_SukChanHoSu.BE.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum MemberErrorCode {
    PASSWORDS_NOT_MATCH("400-1", "비밀번호가 일치하지 않습니다."),
    EMAIL_ALREADY_EXISTS("400-2", "이미 존재하는 이메일입니다."),
    EMAIL_NOT_FOUND("400-3", "존재하지 않는 이메일입니다."),
    PASSWORD_INVALID("400-4", "비밀번호가 일치하지 않습니다.");

    private final String code;
    private final String message;

    MemberErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}
