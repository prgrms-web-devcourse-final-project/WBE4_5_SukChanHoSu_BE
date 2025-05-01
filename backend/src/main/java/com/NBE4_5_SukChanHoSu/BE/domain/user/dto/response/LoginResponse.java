package com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class LoginResponse {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
