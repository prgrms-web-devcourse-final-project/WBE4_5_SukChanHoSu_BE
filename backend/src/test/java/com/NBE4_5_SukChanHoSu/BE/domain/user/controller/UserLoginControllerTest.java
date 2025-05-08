package com.NBE4_5_SukChanHoSu.BE.domain.user.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserLoginRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserSignUpRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Role;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserService;
import com.NBE4_5_SukChanHoSu.BE.global.exception.ServiceException;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class UserLoginControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    @DisplayName("회원가입 성공")
    void joinSuccess() {
        // given
        String email = "joinTest@example.com";
        redisTemplate.opsForValue().set("emailVerify:" + email, "true", 30, TimeUnit.SECONDS);

        UserSignUpRequest requestDto = new UserSignUpRequest();
        requestDto.setEmail(email);
        requestDto.setPassword("testPassword123!");
        requestDto.setPasswordConfirm("testPassword123!");

        // when
        User savedUser = userService.join(requestDto);

        // then
        assertAll(
                () -> assertNotNull(savedUser.getId()),
                () -> assertEquals(requestDto.getEmail(), savedUser.getEmail()),
                () -> assertTrue(passwordEncoder.matches(requestDto.getPassword(), savedUser.getPassword())),
                () -> assertEquals(Role.USER, savedUser.getRole())
        );
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        // given
        String email = "loginuser@example.com";
        String rawPassword = "testPassword123!";

        redisTemplate.opsForValue().set("emailVerify:" + email, "true", 30, TimeUnit.SECONDS);

        UserSignUpRequest signUpDto = new UserSignUpRequest();
        signUpDto.setEmail(email);
        signUpDto.setPassword(rawPassword);
        signUpDto.setPasswordConfirm(rawPassword);
        userService.join(signUpDto);

        // 로그인 시도
        UserLoginRequest loginDto = new UserLoginRequest();
        loginDto.setEmail(email);
        loginDto.setPassword(rawPassword);

        // when
        LoginResponse tokenDto = userService.login(loginDto);

        // then
        assertAll(
                () -> assertNotNull(tokenDto),
                () -> assertNotNull(tokenDto.getAccessToken()),
                () -> assertNotNull(tokenDto.getRefreshToken())
        );
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void loginFail_EmailNotFound() {
        // given
        UserLoginRequest loginDto = new UserLoginRequest();
        loginDto.setEmail("nonexistent@example.com");
        loginDto.setPassword("somePassword");

        // when & then
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.login(loginDto));
        assertEquals("존재하지 않는 이메일입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void loginFail_InvalidPassword() {
        // given
        String email = "wrongpassword@example.com";
        String correctPassword = "correctPassword123!";
        String wrongPassword = "wrongPassword";

        redisTemplate.opsForValue().set("emailVerify:" + email, "true", 30, TimeUnit.SECONDS);

        UserSignUpRequest signUpDto = new UserSignUpRequest();
        signUpDto.setEmail(email);
        signUpDto.setPassword(correctPassword);
        signUpDto.setPasswordConfirm(correctPassword);

        userService.join(signUpDto);

        UserLoginRequest loginDto = new UserLoginRequest();
        loginDto.setEmail(email);
        loginDto.setPassword(wrongPassword);

        // when & then
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.login(loginDto));
        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    }
}
