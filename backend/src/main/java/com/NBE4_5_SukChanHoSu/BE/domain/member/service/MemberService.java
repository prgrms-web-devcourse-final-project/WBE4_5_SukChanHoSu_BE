package com.NBE4_5_SukChanHoSu.BE.domain.member.service;

import com.NBE4_5_SukChanHoSu.BE.domain.member.dto.request.MemberLoginRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.member.dto.request.MemberSignUpRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.member.entity.Member;
import com.NBE4_5_SukChanHoSu.BE.domain.member.entity.MemberErrorCode;
import com.NBE4_5_SukChanHoSu.BE.domain.member.entity.Role;
import com.NBE4_5_SukChanHoSu.BE.domain.member.repository.MemberRepository;
import com.NBE4_5_SukChanHoSu.BE.global.exception.ServiceException;
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
    private final CookieUtil util;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";

    public Member join(MemberSignUpRequestDto requestDto) {
        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            throw new ServiceException(
                    MemberErrorCode.PASSWORDS_NOT_MATCH.getCode(),
                    MemberErrorCode.PASSWORDS_NOT_MATCH.getMessage()
            );
        }
        Member checkMember = memberRepository.findByEmail(requestDto.getEmail());

        if (checkMember != null) {
            throw new ServiceException(
                    MemberErrorCode.EMAIL_ALREADY_EXISTS.getCode(),
                    MemberErrorCode.EMAIL_ALREADY_EXISTS.getMessage()
            );
        }

        Member member = Member.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .role(Role.USER)
                .build();

        memberRepository.save(member);

        return member;
    }

    public JwtTokenDto login(MemberLoginRequestDto requestDto) {
        Member member = memberRepository.findByEmail(requestDto.getEmail());

        if (member == null) {
            throw new ServiceException(
                    MemberErrorCode.EMAIL_NOT_FOUND.getCode(),
                    MemberErrorCode.EMAIL_NOT_FOUND.getMessage()
            );
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new ServiceException(
                    MemberErrorCode.PASSWORD_INVALID.getCode(),
                    MemberErrorCode.PASSWORD_INVALID.getMessage()
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
}
