package com.NBE4_5_SukChanHoSu.BE.domain.user.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserLoginRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserSignUpRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.LoginResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.UserResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserErrorCode;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserSuccessCode;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import com.NBE4_5_SukChanHoSu.BE.global.util.CookieUtil;
import com.NBE4_5_SukChanHoSu.BE.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserLoginController {
    private static final String GOOGLE_AUTHORIZATION_PATH = "/oauth2/authorization/google";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final UserService userService;
    private final CookieUtil cookieUtil;

    @GetMapping("/google/url")
    public String getGoogleLoginUrl(HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        return baseUrl + GOOGLE_AUTHORIZATION_PATH;
    }

    @PostMapping("/join")
    public RsData<UserResponse> join(@RequestBody UserSignUpRequest requestDto) {
        User user = userService.join(requestDto);

        return new RsData<>(
                UserSuccessCode.JOIN_SUCCESS.getCode(),
                UserSuccessCode.JOIN_SUCCESS.getMessage(),
                new UserResponse(user)
        );
    }

    @PostMapping("/login")
    public RsData<LoginResponse> login(@RequestBody UserLoginRequest requestDto, HttpServletResponse response) {
        try {
            LoginResponse loginResponse = userService.login(requestDto);

            cookieUtil.addAccessCookie(loginResponse.getAccessToken(), response);
            cookieUtil.addRefreshCookie(loginResponse.getRefreshToken(), response);

            return new RsData<>(
                    UserSuccessCode.LOGIN_SUCCESS.getCode(),
                    UserSuccessCode.LOGIN_SUCCESS.getMessage(),
                    loginResponse
            );
        } catch (SecurityException e) {
            return new RsData<>("401-UNAUTHORIZED", e.getMessage());
        }
    }

    @PostMapping("/logout")
    public RsData<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        String accessToken = null;
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            accessToken = authHeader.substring(7);
        }

        if (accessToken == null) {
            accessToken = cookieUtil.getAccessTokenFromCookie(request);
        }

        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);

        if (accessToken == null || refreshToken == null) {
            return new RsData<>(
                    UserErrorCode.LOGOUT_FALLED.getCode(),
                    UserErrorCode.LOGOUT_FALLED.getMessage()
            );
        }

        userService.logout(accessToken, refreshToken);

        cookieUtil.deleteAccessTokenFromCookie(response);
        cookieUtil.deleteRefreshTokenFromCookie(response);

        return new RsData<>("200-SUCCESS", "로그아웃 성공");

    }

    @GetMapping("/me")
    public RsData<UserResponse> getProfile() {
        Long id = SecurityUtil.getCurrentUserId();
        String email = SecurityUtil.getCurrentUserEmail();
        User user = SecurityUtil.getCurrentUser();

        return new RsData<>("200-SUCCESS", "프로필 조회 성공", new UserResponse(user));
    }
}
