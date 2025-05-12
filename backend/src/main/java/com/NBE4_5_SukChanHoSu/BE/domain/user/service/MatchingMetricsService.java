package com.NBE4_5_SukChanHoSu.BE.domain.user.service;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.MatchingRepository;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MatchingMetricsService {

    private final MeterRegistry meterRegistry;
    private final MatchingRepository matchingRepository; // 매칭 기록 조회용

    @PostConstruct
    public void registerMatchingMetrics() {
        meterRegistry.gauge("daily_matching_count", this, MatchingMetricsService::getTodayMatchingCount);
    }

    // 오늘 매칭 수 계산
    public long getTodayMatchingCount() {
        return matchingRepository.countByMatchedAtAfter(LocalDate.now().atStartOfDay());
    }
}

