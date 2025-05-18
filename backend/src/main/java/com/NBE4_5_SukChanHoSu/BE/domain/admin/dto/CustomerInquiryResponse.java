package com.NBE4_5_SukChanHoSu.BE.domain.admin.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CustomerInquiryResponse {
    private Long inquiryId;
    private Long userId;
    private String title;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
