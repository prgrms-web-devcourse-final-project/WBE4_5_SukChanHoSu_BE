package com.NBE4_5_SukChanHoSu.BE.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException | ExpiredJwtException e) {
            throw new RuntimeException("유효하지 않거나 만료된 토큰입니다.", e);
        }
    }

    // 토큰에서 이메일(Subject) 추출
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 토큰에서 만료 시간 추출
    public long getRemainingTime(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.getTime() - System.currentTimeMillis(); // 남은 시간 밀리초
    }
}
