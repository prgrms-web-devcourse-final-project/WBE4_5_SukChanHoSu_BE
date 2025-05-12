package com.NBE4_5_SukChanHoSu.BE.domain.likes.service;

import com.NBE4_5_SukChanHoSu.BE.global.exception.redis.RedisSerializationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final LettuceConnectionFactory lettuceConnectionFactory; // LettuceConnectionFactory 주입
    private static final Logger logger = LoggerFactory.getLogger(AlarmService.class);

    private static final String LIKE_STREAM = "like";
    private static final String MATCHING_STREAM = "matching";

    private volatile boolean running = true; // 스레드 실행 상태 플래그

    // 마지막으로 읽은 ID를 저장할 변수
    private String lastLikeId = "0-0"; // 초기값은 "0-0"
    private String lastMatchId = "0-0"; // 초기값은 "0-0"

    // 알림 내역 저장을 위한 맵 (실제로는 데이터베이스 사용)
    private final ConcurrentHashMap<Long, List<Map<String, String>>> notifications = new ConcurrentHashMap<>();

    // 애플리케이션 시작 시 Redis Stream 리스너 시작
    @PostConstruct
    public void init() {
        new Thread(this::startLikeStreamListener).start();
        new Thread(this::startMatchStreamListener).start();
    }

    // 애플리케이션 종료 시 스레드 종료
    @PreDestroy
    public void destroy() {
        running = false; // 스레드 종료 플래그 설정
    }

    // Like Stream 리스너 시작
    public void startLikeStreamListener() {
        while (running) { // running 플래그가 true인 동안만 실행
            try {
                // LettuceConnectionFactory가 실행 중인지 확인
                if (!lettuceConnectionFactory.isRunning()) {
                    lettuceConnectionFactory.start(); // 종료된 경우 재시작
                }

                StreamReadOptions options = StreamReadOptions.empty().count(1);
                StreamOffset<String> offset = StreamOffset.create(LIKE_STREAM, ReadOffset.from(lastLikeId));
                List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream().read(options, offset);

                if (!records.isEmpty()) {
                    for (MapRecord<String, Object, Object> record : records) {
                        Map<Object, Object> value = record.getValue();
                        String jsonEvent = (String) value.get("data"); // JSON 문자열 추출

                        // JSON 문자열을 Map으로 변환
                        try {
                            Map<String, String> likeEvent = objectMapper.readValue(jsonEvent, Map.class);
                            Long fromUserId = Long.valueOf(likeEvent.get("fromUserId"));
                            Long toUserId = Long.valueOf(likeEvent.get("toUserId"));
                            String message = likeEvent.get("message"); // 메시지
                            String time = likeEvent.get("time"); // 시간

                            // 메시지와 시간을 함께 전송
                            Map<String, String> notification = new HashMap<>();
                            notification.put("message", message);
                            notification.put("time", time);
                            sendNotification(toUserId, notification);
                            saveNotification(toUserId, notification);

                            // 마지막으로 읽은 ID 업데이트
                            lastLikeId = record.getId().getValue();
                        } catch (Exception e) {
                            logger.error("JSON 역 직렬화 실패", e);
                        }
                    }
                }
                Thread.sleep(1000); // 1초 대기
            } catch (Exception e) {
                logger.error("Like Stream 처리 중 오류 발생: ", e);
                if (e instanceof IllegalStateException) {
                    lettuceConnectionFactory.start(); // LettuceConnectionFactory 재시작
                }
            }
        }
    }

    // Match Stream 리스너 시작
    public void startMatchStreamListener() {
        while (running) { // running 플래그가 true인 동안만 실행
            try {
                // LettuceConnectionFactory가 실행 중인지 확인
                if (!lettuceConnectionFactory.isRunning()) {
                    lettuceConnectionFactory.start(); // 종료된 경우 재시작
                }

                StreamReadOptions options = StreamReadOptions.empty().count(1);
                StreamOffset<String> offset = StreamOffset.create(MATCHING_STREAM, ReadOffset.from(lastMatchId));
                List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream().read(options, offset);

                if (!records.isEmpty()) {
                    for (MapRecord<String, Object, Object> record : records) {
                        Map<Object, Object> value = record.getValue();
                        String jsonEvent = (String) value.get("data");

                        try {
                            Map<String, String> matchingEvent = objectMapper.readValue(jsonEvent, Map.class);

                            Long maleUserId = Long.valueOf(matchingEvent.get("maleUserId"));
                            Long femaleUserId = Long.valueOf(matchingEvent.get("femaleUserId"));
                            String messageMale = matchingEvent.get("messageMale"); // 남자에게 보낼 메시지
                            String messageFemale = matchingEvent.get("messageFemale"); // 여자에게 보낼 메시지
                            String time = matchingEvent.get("time"); // 시간

                            // 남자 유저에게 보낼 알림
                            Map<String, String> notificationMale = new HashMap<>();
                            notificationMale.put("message", messageMale);
                            notificationMale.put("time", time);
                            sendNotification(maleUserId, notificationMale);
                            saveNotification(maleUserId, notificationMale);

                            // 여자 유저에게 보낼 알림
                            Map<String, String> notificationFemale = new HashMap<>();
                            notificationFemale.put("message", messageFemale);
                            notificationFemale.put("time", time);
                            sendNotification(femaleUserId, notificationFemale);
                            saveNotification(femaleUserId, notificationFemale);

                            // 마지막으로 읽은 ID 업데이트
                            lastMatchId = record.getId().getValue();
                        } catch (Exception e) {
                            logger.error("JSON 역 직렬화 실패", e);
                        }
                    }
                }
                Thread.sleep(1000); // 1초 대기
            } catch (Exception e) {
                logger.error("Match Stream 처리 중 오류 발생: ", e);
                if (e instanceof IllegalStateException) {
                    lettuceConnectionFactory.start(); // LettuceConnectionFactory 재시작
                }
            }
        }
    }

    // WebSocket을 통해 알림 전송
    private void sendNotification(Long userId, Map<String, String> notification) {
        messagingTemplate.convertAndSend("/sub/notifications/" + userId, notification);
        logger.info("메시지: {} - 시간: {}", notification.get("message"), notification.get("time"));
    }

    // 알림 내역 저장
    private void saveNotification(Long userId, Map<String, String> notification) {
        notifications.computeIfAbsent(userId, k -> new ArrayList<>()).add(notification);
    }

    // 알림 목록 조회
    public List<Map<String, String>> getNotifications(Long userId) {
        return notifications.getOrDefault(userId, new ArrayList<>());
    }

    // 알림 읽음 처리
    public void markAsRead(Long userId) {
        notifications.remove(userId);
    }

    // 미확인 알림 개수 조회
    public int getUnreadNotificationCount(Long userId) {
        return notifications.getOrDefault(userId, new ArrayList<>()).size();
    }
}