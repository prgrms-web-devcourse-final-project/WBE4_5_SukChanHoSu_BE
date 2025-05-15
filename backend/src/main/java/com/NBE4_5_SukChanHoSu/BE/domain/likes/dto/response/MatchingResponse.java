package com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.response;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.entity.Matching;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.UserProfileResponse;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchingResponse {
    private Long matchingId;
    private UserProfileResponse maleUser;
    private UserProfileResponse femaleUser;
    private LocalDateTime createdAt;

    public MatchingResponse(Matching matching, int distance) {
        this.matchingId = matching.getMatchingId();
        this.maleUser = new UserProfileResponse(matching.getMaleUser(), distance);
        this.femaleUser = new UserProfileResponse(matching.getFemaleUser(), distance);
//        this.createdAt = matching.getCreatedAt();
    }
}