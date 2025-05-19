package com.NBE4_5_SukChanHoSu.BE.global.jwt.service;

import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import com.NBE4_5_SukChanHoSu.BE.global.exception.security.ExpiredTokenException;
import com.NBE4_5_SukChanHoSu.BE.global.exception.security.InvalidTokenException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final TokenService tokenService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_TYPE = "Bearer";
    private static final String NOT_FOUND = "404";
    private static final String SET_CONTENT_TYPE = "application/json; charset=UTF-8";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String accessToken = resolveToken(httpRequest);

        try {
            if (accessToken != null) {
                tokenService.validateToken(accessToken);
                Authentication authentication = tokenService.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            chain.doFilter(request, response);
        } catch (ExpiredTokenException e) {
            log.warn("토큰 만료 됨 : {}", e.getMessage());
            sendErrorResponse(httpResponse, e.getCode(), e.getMessage());
        } catch (InvalidTokenException e) {
            log.warn("토큰이 존재하지 않음 : {}", e.getMessage());
            sendErrorResponse(httpResponse, e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("알려지지 않은 토큰 에러", e);
            sendErrorResponse(httpResponse, NOT_FOUND, e.getMessage());
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public static void sendErrorResponse(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(Integer.parseInt(code));
        response.setContentType(SET_CONTENT_TYPE);

        RsData<?> rsData = new RsData<>(code, message);
        String responseBody = objectMapper.writeValueAsString(rsData);

        response.getWriter().write(responseBody);
    }
}
