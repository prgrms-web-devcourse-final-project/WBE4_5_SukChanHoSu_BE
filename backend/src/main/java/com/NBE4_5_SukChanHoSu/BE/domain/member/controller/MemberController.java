package com.NBE4_5_SukChanHoSu.BE.domain.member.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.member.dto.MemberDto;
import com.NBE4_5_SukChanHoSu.BE.domain.member.dto.request.MemberLoginRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.member.dto.request.MemberSignUpRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.member.entity.Member;
import com.NBE4_5_SukChanHoSu.BE.domain.member.service.MemberService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import com.NBE4_5_SukChanHoSu.BE.global.jwt.JwtTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/join")
    public RsData<MemberDto> join(@RequestBody MemberSignUpRequestDto requestDto) {
        Member member = memberService.join(requestDto);

        return new RsData<>(
                "200",
                "회원가입이 완료되었습니다.",
                new MemberDto(member)
        );
    }

    @PostMapping("/login")
    public RsData<JwtTokenDto> login(@RequestBody MemberLoginRequestDto requestDto) {

        return new RsData<>(
                "200",
                "회원가입이 완료되었습니다.",
                memberService.login(requestDto)
        );
    }
}
