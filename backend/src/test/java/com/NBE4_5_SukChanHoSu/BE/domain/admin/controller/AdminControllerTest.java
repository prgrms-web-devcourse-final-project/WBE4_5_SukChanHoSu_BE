package com.NBE4_5_SukChanHoSu.BE.domain.admin.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.admin.service.AdminMonitoringService;
import com.NBE4_5_SukChanHoSu.BE.domain.admin.service.AdminService;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
public class AdminControllerTest {

    private MockMvc mockMvc;
    private AdminMonitoringService adminMonitoringService;
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        // 1. AdminMonitoringService Mock 객체 생성 (기존 코드 유지)
        adminMonitoringService = Mockito.mock(AdminMonitoringService.class);

        // 2. AdminService Mock 객체 생성
        AdminService adminService = Mockito.mock(AdminService.class);

        // 3. ReviewService Mock 객체 생성
        ReviewService reviewService = Mockito.mock(ReviewService.class);

        // 4. AdminController 인스턴스 생성 및 Mock 객체 주입
        adminController = new AdminController(adminService, adminMonitoringService, reviewService);

        // 3. MockMvc 빌더 설정
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDailyMatches_returnsOkAndCount() throws Exception {
        // Given
        long mockCount = 15L;
        when(adminMonitoringService.getTodayDailyMatches()).thenReturn(mockCount);

        // When
        mockMvc.perform(get("/api/admin/daily-matches")
                        .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-OK"))
                .andExpect(jsonPath("$.message").value("오늘 일어난 매칭 수 조회 성공"))
                .andExpect(jsonPath("$.data").value(mockCount));

        verify(adminMonitoringService, times(1)).getTodayDailyMatches();
    }

    @Test
    void getDailyMatches_permitAll() throws Exception {
        // Given
        long mockCount = 7L;
        when(adminMonitoringService.getTodayDailyMatches()).thenReturn(mockCount);

        // When
        mockMvc.perform(get("/api/admin/daily-matches")
                        .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-OK"))
                .andExpect(jsonPath("$.message").value("오늘 일어난 매칭 수 조회 성공"))
                .andExpect(jsonPath("$.data").value(mockCount));

        verify(adminMonitoringService, times(1)).getTodayDailyMatches();
    }
}
