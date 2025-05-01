package com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.response;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.Matching;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMatchingResponse {
    private Long matchingId;
    private LocalDateTime matchingTime;
    private UserProfile user;

    public UserMatchingResponse(UserProfile user, Matching matching) {
        this.user = user;
        this.matchingId = matching.getMatchingId();
        this.matchingTime = matching.getMatchingTime();
    }
}