package com.NBE4_5_SukChanHoSu.BE.global.security;

import com.NBE4_5_SukChanHoSu.BE.global.jwt.JwtTokenDto;
import com.NBE4_5_SukChanHoSu.BE.global.jwt.TokenProvider;
import com.NBE4_5_SukChanHoSu.BE.global.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final CookieUtil util;
    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        JwtTokenDto token = tokenProvider.generateToken(authentication);

        util.addCookie("accessToken", token.getAccessToken());
        util.addCookie("refreshToken", token.getRefreshToken());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.sendRedirect("/"); // 로그인 성공 후 리디렉션 URL (필요시 수정)
    }
}