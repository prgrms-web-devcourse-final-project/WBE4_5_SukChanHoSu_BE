package com.NBE4_5_SukChanHoSu.BE.domain.admin.dto;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailResponse {
    private Long id;
    private String email;
    private UserStatus status;
}