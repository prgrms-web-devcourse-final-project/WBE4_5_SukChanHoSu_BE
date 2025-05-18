package com.NBE4_5_SukChanHoSu.BE.global.jwt.responseCode;

import lombok.Getter;

@Getter
public enum JwtErrorCode {
    INVALID_REFRESH_TOKEN("401-1", "유효하지 않은 RefreshToken 입니다."),
    BLACKLISTED_REFRESH_TOKEN("401-2", "로그아웃된 RefreshToken 입니다."),
    INVALID_SIGNATURE("401-3", "잘못된 JWT 서명입니다."),
    EXPIRED_TOKEN("401-4", "만료된 JWT 토큰입니다."),
    EMPTY_CLAIMS("401-5", "JWT claims 가 비어 있습니다."),
    MISSING_AUTHORITY("401-8", "권한 정보가 없는 토큰입니다.");

    private final String code;
    private final String message;

    JwtErrorCode(String code, String message) {
        this.code = code.split("-")[0];
        this.message = message;
    }
}
