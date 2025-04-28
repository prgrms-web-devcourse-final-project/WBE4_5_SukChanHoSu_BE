package com.NBE4_5_SukChanHoSu.BE.domain.likes.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchingResponse {
    private String user1Nickname;
    private String user2Nickname;
    private LocalDateTime matchingTime;
}