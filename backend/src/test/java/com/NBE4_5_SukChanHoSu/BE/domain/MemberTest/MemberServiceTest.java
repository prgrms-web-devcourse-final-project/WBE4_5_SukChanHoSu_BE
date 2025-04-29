//package com.NBE4_5_SukChanHoSu.BE.domain.MemberTest;
//
//import com.NBE4_5_SukChanHoSu.BE.domain.member.dto.request.MemberLoginRequestDto;
//import com.NBE4_5_SukChanHoSu.BE.domain.member.dto.request.MemberSignUpRequestDto;
//import com.NBE4_5_SukChanHoSu.BE.domain.member.entity.Member;
//import com.NBE4_5_SukChanHoSu.BE.domain.member.entity.Role;
//import com.NBE4_5_SukChanHoSu.BE.domain.member.repository.MemberRepository;
//import com.NBE4_5_SukChanHoSu.BE.domain.member.service.MemberService;
//import com.NBE4_5_SukChanHoSu.BE.global.exception.ServiceException;
//import com.NBE4_5_SukChanHoSu.BE.global.jwt.JwtTokenDto;
//import com.NBE4_5_SukChanHoSu.BE.global.jwt.TokenProvider;
//import com.NBE4_5_SukChanHoSu.BE.global.util.CookieUtil;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.*;
//
//@ActiveProfiles("test")
//@SpringBootTest
//@ExtendWith(MockitoExtension.class)
//public class MemberServiceTest {
//    @InjectMocks
//    private MemberService memberService;
//
//    @Mock
//    private MemberRepository memberRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private AuthenticationManagerBuilder authenticationManagerBuilder;
//
//    @Mock
//    private TokenProvider tokenProvider;
//
//    @Mock
//    private CookieUtil util;
//
//    @Mock
//    private AuthenticationManager authenticationManager;
//
//    @Test
//    @DisplayName("회원가입 성공")
//    void joinSuccess() {
//        // given
//        MemberSignUpRequestDto requestDto = new MemberSignUpRequestDto();
//        requestDto.setEmail("test@example.com");
//        requestDto.setPassword("plainPassword");
//        requestDto.setPasswordConfirm("plainPassword");
//
//        when(memberRepository.findByEmail(requestDto.getEmail())).thenReturn(null);
//        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
//
//        Member expectedMember = Member.builder()
//                .email(requestDto.getEmail())
//                .password("encodedPassword")
//                .role(Role.USER)
//                .build();
//
//        when(memberRepository.save(any(Member.class))).thenReturn(expectedMember);
//
//        // when
//        Member actualMember = memberService.join(requestDto);
//
//        // then
//        assertAll(
//                () -> assertNotNull(actualMember),
//                () -> assertEquals("test@example.com", actualMember.getEmail()),
//                () -> assertEquals("encodedPassword", actualMember.getPassword()),
//                () -> assertEquals(Role.USER, actualMember.getRole())
//        );
//        verify(memberRepository).save(any(Member.class));
//    }
//
//    @Test
//    @DisplayName("로그인 성공")
//    void loginSuccess() {
//        // given
//        MemberLoginRequestDto requestDto = new MemberLoginRequestDto();
//        requestDto.setEmail("test@example.com");
//        requestDto.setPassword("plainPassword");
//
//        Member foundMember = Member.builder()
//                .email(requestDto.getEmail())
//                .password("encodedPassword")
//                .role(Role.USER)
//                .build();
//
//        when(memberRepository.findByEmail(requestDto.getEmail())).thenReturn(foundMember);
//        when(passwordEncoder.matches(requestDto.getPassword(), foundMember.getPassword())).thenReturn(true);
//
//        Authentication authentication = mock(Authentication.class);
//        JwtTokenDto expectedToken = new JwtTokenDto(
//                "auth",
//                "accessToken",
//                "refreshToken"
//        );
//
//        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
//        when(tokenProvider.generateToken(authentication)).thenReturn(expectedToken);
//
//        // when
//        JwtTokenDto actualToken = memberService.login(requestDto);
//
//        // then
//        assertAll(
//                () -> assertNotNull(actualToken),
//                () -> assertEquals("accessToken", actualToken.getAccessToken()),
//                () -> assertEquals("refreshToken", actualToken.getRefreshToken())
//        );
//        verify(util).addCookie("access_token", "accessToken");
//        verify(util).addCookie("refresh_token", "refreshToken");
//    }
//
//
//    @Test
//    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
//    void loginFail_EmailNotFound() {
//        // given
//        MemberLoginRequestDto requestDto = new MemberLoginRequestDto();
//        requestDto.setEmail("nonexistent@example.com");
//        requestDto.setPassword("anyPassword");
//
//        given(memberRepository.findByEmail(requestDto.getEmail()))
//                .willReturn(null);
//
//        // when & then
//        ServiceException exception = assertThrows(ServiceException.class, () -> memberService.login(requestDto));
//        assertEquals("존재하지 않는 이메일입니다.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("로그인 실패 - 비밀번호 불일치")
//    void loginFail_InvalidPassword() {
//        // given
//        MemberLoginRequestDto requestDto = new MemberLoginRequestDto();
//        requestDto.setEmail("test@example.com");
//        requestDto.setPassword("wrongPassword");
//
//        Member foundMember = Member.builder()
//                .email(requestDto.getEmail())
//                .password("encodedPassword")
//                .role(Role.USER)
//                .build();
//
//        given(memberRepository.findByEmail(requestDto.getEmail()))
//                .willReturn(foundMember);
//        given(passwordEncoder.matches(requestDto.getPassword(), foundMember.getPassword()))
//                .willReturn(false);
//
//        // when & then
//        ServiceException exception = assertThrows(ServiceException.class, () -> memberService.login(requestDto));
//        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
//    }
//
//
//}
