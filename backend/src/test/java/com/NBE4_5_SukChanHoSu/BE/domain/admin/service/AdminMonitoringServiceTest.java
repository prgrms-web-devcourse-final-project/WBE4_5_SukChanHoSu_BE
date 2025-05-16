package com.NBE4_5_SukChanHoSu.BE.domain.admin.service;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class AdminMonitoringServiceTest {

    private AdminMonitoringService adminMonitoringService;
    private StringRedisTemplate redisTemplate;
    private SimpleMeterRegistry meterRegistry;
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        redisTemplate = Mockito.mock(StringRedisTemplate.class);
        valueOperations = Mockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        adminMonitoringService = new AdminMonitoringService(meterRegistry, redisTemplate);
        adminMonitoringService.initMetrics();
    }

    @Test
    void testIncrementDailyMatches() {
        String todayKey = "admin:daily:matches:" + LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        adminMonitoringService.incrementDailyMatches();

        // Micrometer Counter 검증
        assertEquals(1.0, meterRegistry.get("admin.daily.matches").counter().count());

        // Redis 카운터 증가 검증
        verify(valueOperations).increment(todayKey);
        verify(redisTemplate).expire(todayKey, 1, TimeUnit.DAYS);
    }

    @Test
    void testGetTodayDailyMatches() {
        String todayKey = "admin:daily:matches:" + LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        // Redis 값이 존재하는 경우
        when(valueOperations.get(todayKey)).thenReturn("5");
        long count = adminMonitoringService.getTodayDailyMatches();
        assertEquals(5, count);

        // Redis 값이 없는 경우 (null)
        when(valueOperations.get(todayKey)).thenReturn(null);
        count = adminMonitoringService.getTodayDailyMatches();
        assertEquals(0, count);
    }

    @Test
    void testIncrementTotalUsers() {
        adminMonitoringService.incrementTotalUsers();

        // Micrometer Counter 검증
        assertEquals(1.0, meterRegistry.get("admin.total.users").counter().count());
    }
}