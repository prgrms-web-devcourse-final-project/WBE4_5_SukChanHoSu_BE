package com.NBE4_5_SukChanHoSu.BE.domain.likes.dto;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.Matching;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import lombok.Data;

@Data
public class MatchingResponse {
    private Matching matching;
    private UserProfile maleUser;
    private UserProfile femaleUser;
}