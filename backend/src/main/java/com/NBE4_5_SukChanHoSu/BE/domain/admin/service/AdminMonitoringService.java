package com.NBE4_5_SukChanHoSu.BE.domain.admin.service;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminMonitoringService {
    private final MeterRegistry meterRegistry;

    @PostConstruct
    public void initMetrics() {
        // 초기 카운터 설정
        meterRegistry.counter("admin.daily.matches", "description", "일일 매칭 수");
        meterRegistry.counter("admin.total.users", "description", "총 가입자 수");
        meterRegistry.counter("admin.total.posts", "description", "총 게시글 수");
    }

    public void incrementDailyMatches() {
        meterRegistry.counter("admin.daily.matches").increment();
    }

    public void incrementTotalUsers() {
        meterRegistry.counter("admin.total.users").increment();
    }

    public void incrementTotalPosts() {
        meterRegistry.counter("admin.total.posts").increment();
    }
}
