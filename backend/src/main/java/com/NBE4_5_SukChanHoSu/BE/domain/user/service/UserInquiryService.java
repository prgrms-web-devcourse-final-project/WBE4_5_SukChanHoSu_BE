package com.NBE4_5_SukChanHoSu.BE.domain.user.service;

import com.NBE4_5_SukChanHoSu.BE.domain.admin.entity.CustomerInquiry;
import com.NBE4_5_SukChanHoSu.BE.domain.admin.entity.InquiryStatus;
import com.NBE4_5_SukChanHoSu.BE.domain.admin.repository.CustomerInquiryRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.CustomerInquiryRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.admin.dto.CustomerInquiryResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserInquiryService {
    private final UserRepository userRepository;
    private final CustomerInquiryRepository customerInquiryRepository;

    @Transactional
    public CustomerInquiryResponse createUserInquiry(Long userId, CustomerInquiryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        CustomerInquiry inquiry = CustomerInquiry.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .status(InquiryStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CustomerInquiry savedInquiry = customerInquiryRepository.save(inquiry);
        return toResponse(savedInquiry);
    }

    @Transactional(readOnly = true)
    public List<CustomerInquiryResponse> getUserInquiries(Long userId) {
        return customerInquiryRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private CustomerInquiryResponse toResponse(CustomerInquiry inquiry) {
        return CustomerInquiryResponse.builder()
                .inquiryId(inquiry.getId())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .status(inquiry.getStatus().name())
                .createdAt(inquiry.getCreatedAt())
                .updatedAt(inquiry.getUpdatedAt())
                .build();
    }
}
