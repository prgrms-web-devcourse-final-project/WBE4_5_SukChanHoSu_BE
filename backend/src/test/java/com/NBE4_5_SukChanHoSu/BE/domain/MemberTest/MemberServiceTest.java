package com.NBE4_5_SukChanHoSu.BE.domain.MemberTest;

import com.NBE4_5_SukChanHoSu.BE.domain.member.dto.request.MemberLoginRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.member.dto.request.MemberSignUpRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.member.entity.Member;
import com.NBE4_5_SukChanHoSu.BE.domain.member.entity.Role;
import com.NBE4_5_SukChanHoSu.BE.domain.member.repository.MemberRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.member.service.MemberService;
import com.NBE4_5_SukChanHoSu.BE.global.jwt.JwtTokenDto;
import com.NBE4_5_SukChanHoSu.BE.global.jwt.TokenProvider;
import com.NBE4_5_SukChanHoSu.BE.global.util.CookieUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private CookieUtil util;

    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("회원가입 성공")
    void joinSuccess() {
        // given
        MemberSignUpRequestDto requestDto = new MemberSignUpRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("encodedPassword");

        Member testMember = Member.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        given(passwordEncoder.encode(requestDto.getPassword())).willReturn("encodedPassword");
        given(memberRepository.save(any(Member.class))).willReturn(testMember);

        // when
        Member result = memberService.join(requestDto);

        // then
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(Role.USER, result.getRole());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        // given
        final String AUTHORITIES_KEY = "auth";
        MemberLoginRequestDto requestDto = new MemberLoginRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("encodedPassword");

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());

        Authentication authentication = mock(Authentication.class);
        JwtTokenDto jwtTokenDto = new JwtTokenDto(
                AUTHORITIES_KEY,
                "accessToken",
                "refreshToken"
        );

        given(authenticationManagerBuilder.getObject()).willReturn(authenticationManager);
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(authentication);
        given(tokenProvider.generateToken(authentication)).willReturn(jwtTokenDto);

        // when
        JwtTokenDto result = memberService.login(requestDto);

        // then
        assertNotNull(result);
        assertEquals("accessToken", result.getAccessToken());
        assertEquals("refreshToken", result.getRefreshToken());
        verify(util).addCookie("accessToken", "accessToken");
        verify(util).addCookie("refreshToken", "refreshToken");
    }

    @Test
    @DisplayName("로그인 실패")
    void loginFail() {
        // given
        MemberLoginRequestDto requestDto = new MemberLoginRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("encodedPassword");

        given(authenticationManagerBuilder.getObject()).willReturn(authenticationManager);
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new BadCredentialsException("Bad credentials"));

        // when & then
        assertThrows(BadCredentialsException.class, () -> memberService.login(requestDto));
    }
}
