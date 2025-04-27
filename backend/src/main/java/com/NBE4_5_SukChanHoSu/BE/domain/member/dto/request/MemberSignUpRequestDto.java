package com.NBE4_5_SukChanHoSu.BE.domain.member.dto.request;

import lombok.Data;

@Data
public class MemberSignUpRequestDto {
    private String email;
    private String password;
    private String passwordConfirm;
}
