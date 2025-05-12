package com.NBE4_5_SukChanHoSu.BE.domain.likes.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.service.NoticeService;
import com.NBE4_5_SukChanHoSu.BE.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 알림 목록 조회
    @GetMapping
    public List<Map<String, String>>  getNotifications() {
        Long userId = SecurityUtil.getCurrentUser().getId();
        return noticeService.getNotifications(userId);
    }

    // 알림 확인 처리
    @PostMapping("/read")
    public void markAsRead() {
        Long userId = SecurityUtil.getCurrentUser().getId();
        noticeService.markAsRead(userId);
    }

    // 미확인 알림 개수 조회
    @GetMapping("/count")
    public int getUnreadNotificationCount() {
        Long userId = SecurityUtil.getCurrentUser().getId();
        return noticeService.getUnreadNotificationCount(userId);
    }
}
