package com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.response;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.UserProfileResponse;
import lombok.Data;

import java.util.List;

@Data
public class UserLikeResponse {
    private int size;
    private List<UserProfileResponse> userLikes;

    public UserLikeResponse(List<UserProfileResponse> userProfileResponses) {
        this.size = userProfileResponses.size();
        this.userLikes = userProfileResponses;
    }
}
