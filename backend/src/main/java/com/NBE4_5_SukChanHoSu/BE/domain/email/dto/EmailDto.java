package com.NBE4_5_SukChanHoSu.BE.domain.email.dto;

import lombok.Data;

@Data
public class EmailDto {
    private String mail;
    private String verifyCode;
}