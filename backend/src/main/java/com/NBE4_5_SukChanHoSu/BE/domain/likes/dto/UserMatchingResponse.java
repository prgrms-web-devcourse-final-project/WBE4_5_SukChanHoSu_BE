package com.NBE4_5_SukChanHoSu.BE.domain.likes.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserMatchingResponse {
    private Long userId;
    private String userNickname;
    private LocalDateTime matchingTime;
}