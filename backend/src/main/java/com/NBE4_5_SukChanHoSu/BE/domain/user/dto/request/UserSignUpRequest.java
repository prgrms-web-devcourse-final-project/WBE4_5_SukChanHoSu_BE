package com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpRequest {
    private String email;
    private String password;
    private String passwordConfirm;
}
