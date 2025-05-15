package com.NBE4_5_SukChanHoSu.BE.domain.likes.service;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.controller.SseController;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@DependsOn(value = {"noticeService", "sseController"})
@RequiredArgsConstructor
@Profile("!test")
public class StreamManager {

    private final NoticeService noticeService;
    private final SseController sseController;
    private final RedisTemplate<String, Object> redisTemplate;

    private Thread likeStreamThread;
    private Thread matchStreamThread;

    // 앱 시작시
    @PostConstruct
    public void init() {
        likeStreamThread = new Thread(noticeService::startLikeStreamListener);
        matchStreamThread = new Thread(noticeService::startMatchStreamListener);
        likeStreamThread.start();
        matchStreamThread.start();
    }

    // 앱 종료시
    @PreDestroy
    public void destroy() {
        noticeService.stop();
        sseController.destroy();

        // 스레드 종료
        if (likeStreamThread != null) {
            likeStreamThread.interrupt();
        }
        if (matchStreamThread != null) {
            matchStreamThread.interrupt();
        }

        // 스레드가 종료될 때까지 대기
        // 스레드가 종료될 때까지 대기
        try {
            if (likeStreamThread != null) {
                likeStreamThread.join(); // 스레드가 종료될 때까지 대기
            }
            if (matchStreamThread != null) {
                matchStreamThread.join(); // 스레드가 종료될 때까지 대기
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Redis 종료
        redisTemplate.getConnectionFactory().getConnection().close();
    }
}
