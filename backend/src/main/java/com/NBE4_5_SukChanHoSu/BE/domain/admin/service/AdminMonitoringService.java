package com.NBE4_5_SukChanHoSu.BE.domain.admin.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMonitoringService {
    private final MeterRegistry meterRegistry;
    private final StringRedisTemplate redisTemplate;

    private Counter dailyMatchesCounter;
    private Counter totalUsersCounter;

    private static final String DAILY_MATCHES_KEY_PREFIX = "admin:daily:matches:";

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
        // 1. Micrometer Counter 증가 (모니터링용)
        dailyMatchesCounter.increment();
        // 2. Redis 카운터 증가 (정확한 데이터 저장용)
        String key = getTodayKey();
        redisTemplate.opsForValue().increment(key);
        // Redis 키 TTL 2일로 설정 (데이터 누적 방지 및 자동 삭제)
        redisTemplate.expire(key, 2, TimeUnit.DAYS);
    }

    // 오늘 일일 매칭 수 조회 (Redis 기준)
    public long getTodayDailyMatches() {
        String key = getTodayKey();
        String count = redisTemplate.opsForValue().get(key);
        return count == null ? 0L : Long.parseLong(count);
    }

    private String getTodayKey() {
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        return DAILY_MATCHES_KEY_PREFIX + date;
    }

    public void incrementTotalUsers() {
        totalUsersCounter.increment();
    }

}
