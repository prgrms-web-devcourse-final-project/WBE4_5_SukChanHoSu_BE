package com.NBE4_5_SukChanHoSu.BE.domain.likes.dto;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import lombok.Data;

import java.util.List;

@Data
public class UserLikeResponse {
    private int size;
    private List<UserProfile> UserLikes;
}
