package com.NBE4_5_SukChanHoSu.BE.domain.user.service;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserLoginRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserSignUpRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserErrorCode;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Role;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import com.NBE4_5_SukChanHoSu.BE.global.exception.ServiceException;
import com.NBE4_5_SukChanHoSu.BE.global.exception.user.UserNotFoundException;
import com.NBE4_5_SukChanHoSu.BE.global.jwt.JwtTokenDto;
import com.NBE4_5_SukChanHoSu.BE.global.jwt.TokenProvider;
import com.NBE4_5_SukChanHoSu.BE.global.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final CookieUtil util;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";

    public User join(UserSignUpRequest requestDto) {
        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            throw new ServiceException(
                    UserErrorCode.PASSWORDS_NOT_MATCH.getCode(),
                    UserErrorCode.PASSWORDS_NOT_MATCH.getMessage()
            );
        }
        User checkUser = userRepository.findByEmail(requestDto.getEmail());

        if (checkUser != null) {
            throw new ServiceException(
                    UserErrorCode.EMAIL_ALREADY_EXISTS.getCode(),
                    UserErrorCode.EMAIL_ALREADY_EXISTS.getMessage()
            );
        }

        User user = User.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    public JwtTokenDto login(UserLoginRequest requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail());

        if (user == null) {
            throw new ServiceException(
                    UserErrorCode.EMAIL_NOT_FOUND.getCode(),
                    UserErrorCode.EMAIL_NOT_FOUND.getMessage()
            );
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new ServiceException(
                    UserErrorCode.PASSWORD_INVALID.getCode(),
                    UserErrorCode.PASSWORD_INVALID.getMessage()
            );
        }

        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            JwtTokenDto jwtToken = tokenProvider.generateToken(authentication);

            util.addCookie(ACCESS_TOKEN, jwtToken.getAccessToken());
            util.addCookie(REFRESH_TOKEN, jwtToken.getRefreshToken());

            return jwtToken;
        } catch (Exception e) {
            log.error("예외 발생 -> ", e);
            throw e;
        }
    }

    public User getUserById(Long userId) {
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("401","존재하지 않는 유저입니다."));
        return user;
    }


}
