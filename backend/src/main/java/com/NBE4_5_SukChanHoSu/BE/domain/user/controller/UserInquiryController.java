package com.NBE4_5_SukChanHoSu.BE.domain.user.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.admin.dto.CustomerInquiryResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.CustomerInquiryRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/inquiries")
@RequiredArgsConstructor
public class UserInquiryController {
    private final UserInquiryService userInquiryService;

    @PostMapping
    public CustomerInquiryResponse createUserInquiry(
            @PathVariable Long userId,
            @RequestBody CustomerInquiryRequest request) {
        return userInquiryService.createUserInquiry(userId, request);
    }

    @GetMapping
    public List<CustomerInquiryResponse> getUserInquiries(@PathVariable Long userId) {
        return userInquiryService.getUserInquiries(userId);
    }
}
