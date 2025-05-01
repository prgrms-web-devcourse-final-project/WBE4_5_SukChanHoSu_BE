package com.NBE4_5_SukChanHoSu.BE.domain.user.service;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserLoginRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserSignUpRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.LoginResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.UserResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Role;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserErrorCode;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import com.NBE4_5_SukChanHoSu.BE.global.exception.ServiceException;
import com.NBE4_5_SukChanHoSu.BE.global.exception.user.UserNotFoundException;
import com.NBE4_5_SukChanHoSu.BE.global.jwt.service.TokenService;
import com.NBE4_5_SukChanHoSu.BE.global.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

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

    public LoginResponse login(UserLoginRequest requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail());

        if (user == null) {
            throw new ServiceException(
                    UserErrorCode.EMAIL_NOT_FOUND.getCode(),
                    UserErrorCode.EMAIL_NOT_FOUND.getMessage()
            );
        }

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new ServiceException(
                    UserErrorCode.PASSWORD_INVALID.getCode(),
                    UserErrorCode.PASSWORD_INVALID.getMessage()
            );
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        return tokenService.generateToken(authentication);
    }

    public User getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("401", "존재하지 않는 유저입니다."));
        return user;
    }

    public void logout(String accessToken, String refreshToken) {
        if (accessToken == null || refreshToken == null) {
            throw new RuntimeException("오류");
        }

        String email = tokenService.getEmailFromToken(refreshToken);
        tokenService.deleteRefreshToken(email);

        long expirationTime = tokenService.getExpirationTimeFromToken(accessToken);
        tokenService.addToBlacklist(accessToken, expirationTime);
    }

    public UserResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);

        if(user == null) {
            throw new RuntimeException("존재하지 않는 유저");
        }

        return new UserResponse(user);
    }
}
