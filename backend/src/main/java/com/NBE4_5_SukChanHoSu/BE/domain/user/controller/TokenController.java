package com.NBE4_5_SukChanHoSu.BE.domain.user.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.user.responseCode.UserSuccessCode;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import com.NBE4_5_SukChanHoSu.BE.global.jwt.dto.TokenResponse;
import com.NBE4_5_SukChanHoSu.BE.global.jwt.service.TokenService;
import com.NBE4_5_SukChanHoSu.BE.global.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
@Tag(name = "Token", description = "AccessToken과 RefreshToken 관련 API")
public class TokenController {
    private final CookieUtil cookieUtil;
    private final TokenService tokenService;

    @PostMapping("/reissue")
    @Operation(summary = "AccessToken 재발급", description = "RefreshToken 을 통해 AccessToken 재발급")
    public RsData<TokenResponse> reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);

        TokenResponse tokenResponse = tokenService.reissueAccessToken(refreshToken);

        cookieUtil.addAccessCookie(tokenResponse.getAccessToken(), request, response);

        return new RsData<>(
                UserSuccessCode.REISSUE_SUCCESS.getCode(),
                UserSuccessCode.REISSUE_SUCCESS.getMessage(),
                tokenResponse
        );
    }
}
