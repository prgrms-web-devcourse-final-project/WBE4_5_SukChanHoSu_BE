package com.NBE4_5_SukChanHoSu.BE.domain.admin.dto;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserStatus;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    private UserStatus status;
}
