package com.NBE4_5_SukChanHoSu.BE.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtil {
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // 7일
    @Value("${jwt.expiration.access-token}")
    private int accessTokenExpiration;

    @Value("${jwt.expiration.refresh-token}")
    private int refreshTokenExpiration;

    @Value("${jwt.cookie-domain}")
    private String cookieDomain;

    @Value("${jwt.cookie-path}")
    private String cookiePath;

    public void addAccessCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(ACCESS_TOKEN, token);
        cookie.setMaxAge(accessTokenExpiration);
        cookie.setPath(cookiePath);
        cookie.setDomain(cookieDomain);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    public void addRefreshCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN, token);
        cookie.setMaxAge(refreshTokenExpiration);
        cookie.setPath(cookiePath);
        cookie.setDomain(cookieDomain);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    public String getAccessTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ACCESS_TOKEN.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void deleteAccessTokenFromCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(ACCESS_TOKEN, null);
        cookie.setMaxAge(0);
        cookie.setPath(cookiePath);
        cookie.setDomain(cookieDomain);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    public void deleteRefreshTokenFromCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN, null);
        cookie.setMaxAge(0);
        cookie.setPath(cookiePath);
        cookie.setDomain(cookieDomain);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
}
