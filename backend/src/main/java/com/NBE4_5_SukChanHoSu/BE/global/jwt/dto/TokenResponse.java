package com.NBE4_5_SukChanHoSu.BE.global.jwt.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class TokenResponse {
    private String accessToken;
    private String refreshToken;

    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
