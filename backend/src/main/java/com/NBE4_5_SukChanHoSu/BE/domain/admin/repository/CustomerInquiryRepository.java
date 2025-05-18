package com.NBE4_5_SukChanHoSu.BE.domain.admin.repository;

import com.NBE4_5_SukChanHoSu.BE.domain.admin.entity.CustomerInquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerInquiryRepository extends JpaRepository<CustomerInquiry, Long> {
    List<CustomerInquiry> findByUserId(Long userId);
}
