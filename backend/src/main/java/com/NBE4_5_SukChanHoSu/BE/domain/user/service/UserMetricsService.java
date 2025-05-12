package com.NBE4_5_SukChanHoSu.BE.domain.user.service;

import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserMetricsService {

    private final MeterRegistry meterRegistry;
    private final UserRepository userRepository; // ✔ User 엔티티용 리포지토리 사용

    @PostConstruct
    public void registerUserMetrics() {
        meterRegistry.gauge("daily_signups", this, UserMetricsService::getTodaySignupCount);
    }

    public long getTodaySignupCount() {
        // ✔ createdAt 컬럼이 LocalDateTime 형태라고 가정
        return userRepository.countByCreatedAtAfter(LocalDate.now().atStartOfDay());
    }
}
