package com.NBE4_5_SukChanHoSu.BE.domain.admin.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMonitoringService {
    private final MeterRegistry meterRegistry;

    private Counter dailyMatchesCounter;
    private Counter totalUsersCounter;

    @PostConstruct
    public void initMetrics() {
        // 카운터를 명확히 인스턴스화 해서 필드에 할당
        dailyMatchesCounter = Counter.builder("admin.daily.matches")
                .description("일일 매칭 수")
                .register(meterRegistry);

        totalUsersCounter = Counter.builder("admin.total.users")
                .description("총 가입자 수")
                .register(meterRegistry);
    }

    public void incrementDailyMatches() {
        log.info("incrementDailyMatches 호출됨");
        dailyMatchesCounter.increment();
    }

    public void incrementTotalUsers() {
        totalUsersCounter.increment();
    }
}
