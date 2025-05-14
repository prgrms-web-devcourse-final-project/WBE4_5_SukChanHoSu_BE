//package com.NBE4_5_SukChanHoSu.BE.domain.likes.service;
//
//import com.NBE4_5_SukChanHoSu.BE.domain.likes.controller.SseController;
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.stereotype.Component;
//
//@Component
//@DependsOn({"noticeService","sseController"})
//@RequiredArgsConstructor
//public class StreamManager {
//
//    private final NoticeService noticeService;
//    private final SseController sseController;
//
//    // 앱 시작시
//    @PostConstruct
//    public void init() {
//        new Thread(noticeService::startLikeStreamListener).start();
//        new Thread(noticeService::startMatchStreamListener).start();
//    }
//
//    // 앱 종료시
//    @PreDestroy
//    public void destroy() {
//        noticeService.stop();
//        sseController.destroy();
//    }
//}
