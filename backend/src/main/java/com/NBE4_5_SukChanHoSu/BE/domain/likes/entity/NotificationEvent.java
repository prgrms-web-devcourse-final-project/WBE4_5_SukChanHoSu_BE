package com.NBE4_5_SukChanHoSu.BE.domain.likes.entity;

import lombok.Getter;

@Getter
public class NotificationEvent {
    private final Long userId; // 알림을 받을 사용자 ID
    private final String message; // 알림 메시지

    public NotificationEvent(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }
}
