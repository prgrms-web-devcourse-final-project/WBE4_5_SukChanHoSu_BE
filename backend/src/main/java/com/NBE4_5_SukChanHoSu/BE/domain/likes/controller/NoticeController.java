package com.NBE4_5_SukChanHoSu.BE.domain.likes.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.service.NoticeService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import com.NBE4_5_SukChanHoSu.BE.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 알림 목록 조회
    @GetMapping
    public RsData<List<Map<String, String>>>  getNotifications(){
        Long userId = SecurityUtil.getCurrentUser().getId();
        List<Map<String, String>> responses = noticeService.getNotifications(userId);
        return new RsData<>("200","알림 목록 조회", responses);
    }

    // 알림 확인 처리
    @PostMapping("/read")
    public RsData<String> markAsRead() {
        Long userId = SecurityUtil.getCurrentUser().getId();
        noticeService.markAsRead(userId);
        return new RsData<>("200","완료");
    }

    // 미확인 알림 개수 조회
    @GetMapping("/count")
    public RsData<Integer> getUnreadNotificationCount() {
        Long userId = SecurityUtil.getCurrentUser().getId();
        int count = noticeService.getUnreadNotificationCount(userId);
        return new RsData<>("200","미확인 알림 갯수", count);
    }
}
