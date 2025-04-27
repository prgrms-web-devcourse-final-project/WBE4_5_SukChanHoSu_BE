package com.NBE4_5_SukChanHoSu.BE.domain.member.dto;

import com.NBE4_5_SukChanHoSu.BE.domain.member.entity.Member;
import lombok.Data;

@Data
public class MemberDto {
    private String email;
    private String password;

    public MemberDto(Member member) {
        this.email = member.getEmail();
        this.password = member.getPassword();
    }
}
