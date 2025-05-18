package com.NBE4_5_SukChanHoSu.BE.domain.admin.dto;

import lombok.Data;

@Data
public class CustomerInquiryRequest {
    private Long userId;
    private String title;
    private String content;
}
