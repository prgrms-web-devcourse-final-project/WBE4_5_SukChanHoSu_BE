package com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.response;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.entity.Matching;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.UserProfileResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMatchingResponse {
    private int size;
    int totalPages;
    private List<UserProfileResponse> matchings;

    public UserMatchingResponse(List<UserProfileResponse> userProfileResponses,int totalPages) {
        this.matchings = userProfileResponses;
        this.totalPages = totalPages;
    }
}