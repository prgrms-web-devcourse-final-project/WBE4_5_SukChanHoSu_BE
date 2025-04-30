package com.NBE4_5_SukChanHoSu.BE.domain.likes.dto;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import lombok.Data;


@Data
public class LikeResponse {
    private UserProfile fromUser;
    private UserProfile toUser;
}
