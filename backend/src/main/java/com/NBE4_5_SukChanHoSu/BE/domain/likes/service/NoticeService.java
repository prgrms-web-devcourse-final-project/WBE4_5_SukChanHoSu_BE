package com.NBE4_5_SukChanHoSu.BE.domain.likes.service;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Gender;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserProfileRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import com.NBE4_5_SukChanHoSu.BE.global.exception.redis.RedisSerializationException;
import com.NBE4_5_SukChanHoSu.BE.global.exception.user.UserNotFoundException;
import com.NBE4_5_SukChanHoSu.BE.global.util.DateUtils;
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
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final LettuceConnectionFactory lettuceConnectionFactory; // Redis 연결 관리
    private final UserLikeService userLikeService;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(NoticeService.class);
    private static final String LIKE_STREAM = "like";
    private static final String MATCHING_STREAM = "matching";

    private volatile boolean running = true; // 스레드 실행 상태 플래그

    private String lastLikeId = "0-0"; // 초기값
    private String lastMatchId = "0-0"; // 초기값

    private final ConcurrentHashMap<Long, List<Map<String, String>>> notifications = new ConcurrentHashMap<>();

    // 앱 시작시
    @PostConstruct
    public void init() {
        new Thread(this::startLikeStreamListener).start();
        new Thread(this::startMatchStreamListener).start();
    }

    // 앱 종료시
    @PreDestroy
    public void destroy() {
        running = false; // 스레드 종료 플래그
    }

    // Like Stream 리스너 시작
    public void startLikeStreamListener() {
        while (running) { // 앱이 작동중일때만 실행
            try {
                // redis 연결이 끊어졌는지 확인
                if (!lettuceConnectionFactory.isRunning()) {
                    // 재연결
                    lettuceConnectionFactory.start();
                }

//                StreamReadOptions options = StreamReadOptions.empty().count(1);
//                // 마지막으로 읽은 Id 부터 읽음
//                StreamOffset<String> offset = StreamOffset.create(LIKE_STREAM, ReadOffset.from(lastLikeId));
//                List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream().read(options, offset);

                // 블로킹 읽기 옵션 설정 (10초 대기)
                StreamReadOptions options = StreamReadOptions.empty().block(Duration.ofSeconds(10)).count(1);
                // 마지막으로 읽은 ID부터 읽기
                StreamOffset<String> offset = StreamOffset.create(LIKE_STREAM, ReadOffset.from(lastLikeId));
                // Redis Stream에서 데이터 읽기
                List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream().read(options, offset);

                if (!records.isEmpty()) {
                    for (MapRecord<String, Object, Object> record : records) { // record = ID:Content
                        Map<Object, Object> value = record.getValue();  // value = Content
                        String jsonEvent = (String) value.get("data"); // Content에서 data(Json 문자열) 추출

                        // JSON 문자열을 Map으로 변환
                        try {
                            Map<String, String> likeEvent = objectMapper.readValue(jsonEvent, Map.class);
                            Long toUserId = Long.valueOf(likeEvent.get("toUserId"));
                            String message = likeEvent.get("message"); // 메시지
                            String time = likeEvent.get("time"); // 시간

                            // 메시지와 시간을 함께 전송
                            Map<String, String> notification = new HashMap<>();
                            notification.put("message", message);
                            notification.put("time", time);
                            sendNotification(toUserId, notification);   // webSocket을 통해 알림 전송
                            saveNotification(toUserId, notification);   // 저장

                            // 마지막으로 읽은 ID 업데이트
                            lastLikeId = record.getId().getValue();
                        } catch (Exception e) {
                            logger.error("JSON 역 직렬화 실패", e);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Like Stream 처리 중 오류 발생: ", e);
            }
        }
    }

    // Match Stream 리스너 시작
    public void startMatchStreamListener() {
        while (running) {
            try {
                if (!lettuceConnectionFactory.isRunning()) {
                    lettuceConnectionFactory.start(); // 재시작
                }

                // 블로킹 읽기 옵션 설정 (10초 대기)
                StreamReadOptions options = StreamReadOptions.empty().block(Duration.ofSeconds(10)).count(1);
                // 마지막으로 읽은 ID부터 읽기
                StreamOffset<String> offset = StreamOffset.create(MATCHING_STREAM, ReadOffset.from(lastMatchId));
                // Redis Stream에서 데이터 읽기
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
            } catch (Exception e) {
                logger.error("Match Stream 처리 중 오류 발생: ", e);
            }
        }
    }

    // WebSocket을 통해 알림 전송(경로: /sub/notifications/{userId})
    private void sendNotification(Long userId, Map<String, String> notification) {
        messagingTemplate.convertAndSend("/sub/notifications/" + userId, notification.get("message"));
        logger.info("메시지: {}", notification.get("message"));
    }

    // 알림 내역 저장
    private void saveNotification(Long userId, Map<String, String> notification) {
        notifications.computeIfAbsent(userId, k -> new ArrayList<>()).add(notification);
    }

    // 알림 목록 조회, 시간 변환
    public List<Map<String, String>> getNotifications(Long userId) throws ParseException {
        List<Map<String, String>> updateNotifications = notifications.getOrDefault(userId, new ArrayList<>());
        List<Map<String, String>> result = new ArrayList<>();

        for (Map<String, String> notification : updateNotifications) {
            Map<String, String> updated = new HashMap<>();
            try {
                Date time = DateUtils.parseDate(notification.get("time")); // 날짜 문자열을 Date로 변환
                updated.put("message", notification.get("message"));
                updated.put("timeAgo", DateUtils.getTimeAgo(time)); // time -> timeAgo 로 변환
                result.add(updated);
            } catch (ParseException e) {
                // 날짜 파싱 실패 시 로그 출력
                System.err.println("날짜 파싱 실패: " + notification.get("time"));
            }
        }

        return result;
    }

    // 알림 읽음 처리
    public void markAsRead(Long userId) {
        notifications.remove(userId);
        deleteLikeStreamFromRedis(userId);
        deleteMatchingStreamFromRedis(userId);
    }

    // 미확인 알림 개수 조회
    public int getUnreadNotificationCount(Long userId) {
        return notifications.getOrDefault(userId, new ArrayList<>()).size();
    }

    // 레디스에서 삭제(라이크 이벤트)
    private void deleteLikeStreamFromRedis(Long userId) {
        StreamReadOptions options = StreamReadOptions.empty().count(100); // 100개씩
        StreamOffset<String> offset = StreamOffset.create(LIKE_STREAM, ReadOffset.from("0-0")); // 처음부터
        List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream().read(options, offset);

        if (!records.isEmpty()) {
            for (MapRecord<String, Object, Object> record : records) { // record = ID:Content
                Map<Object, Object> value = record.getValue();  // value = Content
                String jsonEvent = (String) value.get("data"); // Content에서 data(Json 문자열) 추출

                try {
                    Map<String, String> event = objectMapper.readValue(jsonEvent, Map.class);
                    Long toUserId = Long.valueOf(event.get("toUserId"));

                    if(userId.equals(toUserId)) {
                        // redisStream에서 삭제
                        redisTemplate.opsForStream().delete(LIKE_STREAM,record.getId().getValue());
                    }
                } catch (Exception e) {
                    throw new RedisSerializationException("500", "JSON 역 직렬화 실패");
                }

            }
        }
    }

    // 레디스에서 삭제(매칭)
    private void deleteMatchingStreamFromRedis(Long userId) {
        StreamReadOptions options = StreamReadOptions.empty().count(100); // 100개씩
        StreamOffset<String> offset = StreamOffset.create(MATCHING_STREAM, ReadOffset.from("0-0")); // 처음부터
        List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream().read(options, offset);
        if (!records.isEmpty()) {
            // 남자
            if(isMale(userId)) {
                for (MapRecord<String, Object, Object> record : records) { // record = ID:Content
                    Map<Object, Object> value = record.getValue();  // value = Content
                    String jsonEvent = (String) value.get("data"); // Content에서 data(Json 문자열) 추출

                    try {
                        Map<String, String> event = objectMapper.readValue(jsonEvent, Map.class);
                        Long maleUserId = Long.valueOf(event.get("maleUserId"));

                        if(userId.equals(maleUserId)) {
                            // redisStream에서 삭제
                            redisTemplate.opsForStream().delete(MATCHING_STREAM,record.getId().getValue());
                        }
                    } catch (Exception e) {
                        throw new RedisSerializationException("500", "JSON 역 직렬화 실패");
                    }

                }
            } else{
                // 여자
                for (MapRecord<String, Object, Object> record : records) { // record = ID:Content
                    Map<Object, Object> value = record.getValue();  // value = Content
                    String jsonEvent = (String) value.get("data"); // Content에서 data(Json 문자열) 추출

                    try {
                        Map<String, String> event = objectMapper.readValue(jsonEvent, Map.class);
                        Long femaleUserId = Long.valueOf(event.get("femaleUserId"));

                        if(userId.equals(femaleUserId)) {
                            // redisStream에서 삭제
                            redisTemplate.opsForStream().delete(MATCHING_STREAM,record.getId().getValue());
                        }
                    } catch (Exception e) {
                        throw new RedisSerializationException("500", "JSON 역 직렬화 실패");
                    }
                }
            }
        }
    }

    boolean isMale(Long userId) {
        UserProfile profile = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("404","존재하지 않는 유저"))
                .getUserProfile();

        return profile.getGender().equals(Gender.Male);
    }

}