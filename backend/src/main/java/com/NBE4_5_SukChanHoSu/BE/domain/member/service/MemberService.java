package com.NBE4_5_SukChanHoSu.BE.domain.member.service;

import com.NBE4_5_SukChanHoSu.BE.domain.member.dto.request.MemberSignUpRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.member.entity.Member;
import com.NBE4_5_SukChanHoSu.BE.domain.member.entity.Role;
import com.NBE4_5_SukChanHoSu.BE.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member join(MemberSignUpRequestDto memberSignUpRequestDto) {
        Member member = Member.builder()
                .email(memberSignUpRequestDto.getEmail())
                .password(passwordEncoder.encode(memberSignUpRequestDto.getPassword()))
                .role(Role.USER)
                .build();

        memberRepository.save(member);

        return member;
    }
}
