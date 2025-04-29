package com.NBE4_5_SukChanHoSu.BE.domain.MemberTest;

import com.NBE4_5_SukChanHoSu.BE.domain.member.dto.request.MemberLoginRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.member.dto.request.MemberSignUpRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.member.entity.Member;
import com.NBE4_5_SukChanHoSu.BE.domain.member.entity.Role;
import com.NBE4_5_SukChanHoSu.BE.domain.member.repository.MemberRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.member.service.MemberService;
import com.NBE4_5_SukChanHoSu.BE.global.exception.ServiceException;
import com.NBE4_5_SukChanHoSu.BE.global.jwt.JwtTokenDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공")
    void joinSuccess() {
        // given
        MemberSignUpRequestDto requestDto = new MemberSignUpRequestDto();
        requestDto.setEmail("testuser@example.com");
        requestDto.setPassword("testPassword123!");
        requestDto.setPasswordConfirm("testPassword123!");

        // when
        Member savedMember = memberService.join(requestDto);

        // then
        assertAll(
                () -> assertNotNull(savedMember.getId()),
                () -> assertEquals(requestDto.getEmail(), savedMember.getEmail()),
                () -> assertTrue(passwordEncoder.matches(requestDto.getPassword(), savedMember.getPassword())),
                () -> assertEquals(Role.USER, savedMember.getRole())
        );
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        // given
        String email = "loginuser@example.com";
        String rawPassword = "testPassword123!";

        // 회원가입 먼저
        MemberSignUpRequestDto signUpDto = new MemberSignUpRequestDto();
        signUpDto.setEmail(email);
        signUpDto.setPassword(rawPassword);
        signUpDto.setPasswordConfirm(rawPassword);
        memberService.join(signUpDto);

        // 로그인 시도
        MemberLoginRequestDto loginDto = new MemberLoginRequestDto();
        loginDto.setEmail(email);
        loginDto.setPassword(rawPassword);

        // when
        JwtTokenDto tokenDto = memberService.login(loginDto);

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
        MemberLoginRequestDto loginDto = new MemberLoginRequestDto();
        loginDto.setEmail("nonexistent@example.com");
        loginDto.setPassword("somePassword");

        // when & then
        ServiceException exception = assertThrows(ServiceException.class, () -> memberService.login(loginDto));
        assertEquals("존재하지 않는 이메일입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void loginFail_InvalidPassword() {
        // given
        String email = "wrongpassword@example.com";
        String correctPassword = "correctPassword123!";
        String wrongPassword = "wrongPassword";

        // 회원가입 먼저
        MemberSignUpRequestDto signUpDto = new MemberSignUpRequestDto();
        signUpDto.setEmail(email);
        signUpDto.setPassword(correctPassword);
        signUpDto.setPasswordConfirm(correctPassword);
        memberService.join(signUpDto);

        // 잘못된 비밀번호로 로그인 시도
        MemberLoginRequestDto loginDto = new MemberLoginRequestDto();
        loginDto.setEmail(email);
        loginDto.setPassword(wrongPassword);

        // when & then
        ServiceException exception = assertThrows(ServiceException.class, () -> memberService.login(loginDto));
        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    }
}
