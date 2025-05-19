package com.NBE4_5_SukChanHoSu.BE.global.jwt.service;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.LoginResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Role;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import com.NBE4_5_SukChanHoSu.BE.global.exception.security.BlacklistedTokenException;
import com.NBE4_5_SukChanHoSu.BE.global.exception.security.ExpiredTokenException;
import com.NBE4_5_SukChanHoSu.BE.global.exception.security.InvalidTokenException;
import com.NBE4_5_SukChanHoSu.BE.global.jwt.dto.TokenResponse;
import com.NBE4_5_SukChanHoSu.BE.global.jwt.responseCode.JwtErrorCode;
import com.NBE4_5_SukChanHoSu.BE.global.security.PrincipalDetails;
import com.NBE4_5_SukChanHoSu.BE.global.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TokenService {
    private final Key key;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Value("${jwt.expiration.access-token}")
    private int accessTokenExpiration;
    @Value("${jwt.expiration.refresh-token}")
    private int refreshTokenExpiration;


    public TokenService(@Value("${jwt.secret}") String secretKey, JwtUtil jwtUtil, UserRepository userRepository, RedisTemplate<String, String> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public LoginResponse generateToken(Authentication authentication) {
        String accessToken = createAccessToken(authentication.getName());
        String refreshToken = createRefreshToken(authentication.getName());

        return LoginResponse.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String createAccessToken(String email) {
        User user = userRepository.findByEmail(email);
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .claim(AUTHORITIES_KEY, user.getRole().getKey())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .claim(AUTHORITIES_KEY, Role.USER)
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new InvalidTokenException(
                    JwtErrorCode.MISSING_AUTHORITY.getCode(),
                    JwtErrorCode.MISSING_AUTHORITY.getMessage()
            );
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        User user = userRepository.findByEmail(claims.getSubject());
        UserDetails principal = new PrincipalDetails(user);

        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    public void validateToken(String accessToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken);
        } catch (SecurityException | MalformedJwtException e) {
            throw new InvalidTokenException(
                    JwtErrorCode.INVALID_SIGNATURE.getCode(),
                    JwtErrorCode.INVALID_SIGNATURE.getMessage()
            );
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException(
                    JwtErrorCode.EXPIRED_TOKEN.getCode(),
                    JwtErrorCode.EXPIRED_TOKEN.getMessage()
            );
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException(
                    JwtErrorCode.EMPTY_CLAIMS.getCode(),
                    JwtErrorCode.EMPTY_CLAIMS.getMessage()
            );
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String getEmailFromToken(String token) {
        return jwtUtil.extractUsername(token);
    }

    public long getExpirationTimeFromToken(String token) {
        return jwtUtil.getRemainingTime(token);
    }

    public void addToBlacklist(String accessToken, long expirationTime) {
        String key = BLACKLIST_PREFIX + accessToken;
        redisTemplate.opsForValue().set(key, BLACKLIST_PREFIX, expirationTime, TimeUnit.MILLISECONDS);
    }

    public TokenResponse reissueAccessToken(String refreshToken) {
        try {
            validateToken(refreshToken);
        } catch (InvalidTokenException | ExpiredTokenException e) {
            throw new InvalidTokenException(
                    JwtErrorCode.INVALID_REFRESH_TOKEN.getCode(),
                    JwtErrorCode.INVALID_REFRESH_TOKEN.getMessage()
            );
        }

        String email = getEmailFromToken(refreshToken);

        String isBlacklisted = redisTemplate.opsForValue().get(refreshToken);
        if (isBlacklisted != null) {
            throw new BlacklistedTokenException(
                    JwtErrorCode.BLACKLISTED_REFRESH_TOKEN.getCode(),
                    JwtErrorCode.BLACKLISTED_REFRESH_TOKEN.getMessage()
            );
        }

        String newAccessToken = createAccessToken(email);

        return new TokenResponse(newAccessToken, refreshToken);
    }

}

