package com.NBE4_5_SukChanHoSu.BE.domain.admin.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.admin.dto.CustomerInquiryRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.admin.dto.CustomerInquiryResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.admin.entity.InquiryStatus;
import com.NBE4_5_SukChanHoSu.BE.domain.admin.service.CustomerInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class CustomerInquiryController {
    private final CustomerInquiryService customerInquiryService;

    @GetMapping("/{userId}")
    public List<CustomerInquiryResponse> getUserInquiries(@PathVariable Long userId) {
        return customerInquiryService.getInquiriesByUser(userId);
    }

    @GetMapping("/detail/{inquiryId}")
    public CustomerInquiryResponse getInquiryDetail(@PathVariable Long inquiryId) {
        return customerInquiryService.getInquiryDetail(inquiryId);
    }

    @PatchMapping("/{inquiryId}/status")
    public void updateInquiryStatus(@PathVariable Long inquiryId, @RequestParam InquiryStatus status) {
        customerInquiryService.updateInquiryStatus(inquiryId, status);
    }
}
