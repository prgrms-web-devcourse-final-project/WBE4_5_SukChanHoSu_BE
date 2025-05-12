package com.NBE4_5_SukChanHoSu.BE.domain.likes.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.service.AlarmService;
import com.NBE4_5_SukChanHoSu.BE.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alarm")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    // 알림 목록 조회
    @GetMapping
    public List<Map<String, String>>  getNotifications() {
        Long userId = SecurityUtil.getCurrentUser().getId();
        return alarmService.getNotifications(userId);
    }

    // 알림 확인 처리
    @PostMapping("/read")
    public void markAsRead() {
        Long userId = SecurityUtil.getCurrentUser().getId();
        alarmService.markAsRead(userId);
    }

    // 미확인 알림 개수 조회
    @GetMapping("/count")
    public int getUnreadNotificationCount() {
        Long userId = SecurityUtil.getCurrentUser().getId();
        return alarmService.getUnreadNotificationCount(userId);
    }
}
