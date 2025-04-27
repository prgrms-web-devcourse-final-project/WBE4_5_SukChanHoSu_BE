package com.NBE4_5_SukChanHoSu.BE.domain.member.service;

import com.NBE4_5_SukChanHoSu.BE.domain.member.dto.request.MemberLoginRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.member.dto.request.MemberSignUpRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.member.entity.Member;
import com.NBE4_5_SukChanHoSu.BE.domain.member.entity.Role;
import com.NBE4_5_SukChanHoSu.BE.domain.member.repository.MemberRepository;
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
public class MemberService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final CookieUtil util;

    public Member join(MemberSignUpRequestDto memberSignUpRequestDto) {
        Member member = Member.builder()
                .email(memberSignUpRequestDto.getEmail())
                .password(passwordEncoder.encode(memberSignUpRequestDto.getPassword()))
                .role(Role.USER)
                .build();

        memberRepository.save(member);

        return member;
    }

    public JwtTokenDto login(MemberLoginRequestDto requestDto) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            JwtTokenDto jwtToken = tokenProvider.generateToken(authentication);

            util.addCookie("accessToken", jwtToken.getAccessToken());
            util.addCookie("refreshToken", jwtToken.getRefreshToken());

            return jwtToken;
        } catch (Exception e) {
            log.error("🚨 authenticationManager.authenticate() 예외 발생!", e);
            throw e;
        }
    }
}
