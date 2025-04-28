package com.NBE4_5_SukChanHoSu.BE.domain.likes.dto;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import lombok.Data;

import java.util.List;

@Data
public class LikeResponse {
//    private Long fromUserId;
//    private String fromUserNickname;
//    private Long toUserId;
//    private String toUserNickname;
    private UserProfile toUser;
    private String Message;
}
