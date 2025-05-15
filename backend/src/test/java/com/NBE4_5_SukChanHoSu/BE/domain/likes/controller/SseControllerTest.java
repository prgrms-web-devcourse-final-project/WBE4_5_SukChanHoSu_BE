package com.NBE4_5_SukChanHoSu.BE.domain.likes.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.entity.NotificationEvent;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.service.StreamManager;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserLoginRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.LoginResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserService;
import com.NBE4_5_SukChanHoSu.BE.global.config.BaseTestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@BaseTestConfig
class SseControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private SseController sseController;
    @Autowired
    private LettuceConnectionFactory lettuceConnectionFactory; // Redis 연결 관리

    private static String accessToken;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        login();
        connectRedis();
    }

    @DisplayName("Redis 연결")
    public void connectRedis(){
        if (!lettuceConnectionFactory.isRunning()) {
            lettuceConnectionFactory.start(); // 재시작
        }
    }

    @DisplayName("로그인")
    void login() {
        // given
        String email = "initUser1@example.com";
        String rawPassword = "testPassword123!";

        // 로그인
        UserLoginRequest loginDto = new UserLoginRequest();
        loginDto.setEmail(email);
        loginDto.setPassword(rawPassword);

        // when
        LoginResponse tokenDto = userService.login(loginDto);
        accessToken = tokenDto.getAccessToken();
    }

    @Test
    @DisplayName("SSE 연결")
    void createConnection() throws Exception {
        // Given
        Long userId = 1L;

        // When
        mvc.perform(get("/api/sse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
//                .andExpect(jsonPath("$.code").value("200"))
//                .andExpect(jsonPath("$.message",containsString("미확인 알림 갯수")))
//                .andExpect(jsonPath("$.data").value(prevData+1));   // 이전 값보다 1큼

        // Then
        SseEmitter emitter = sseController.getEmitters().get(userId);
        // 연결 생성 검증
        assertNotNull(emitter);
    }

    @Test
    @DisplayName("알림 전송")
    void sendNotification() throws Exception {
        // Given
        Long userId = 1L;

        // When
        mvc.perform(get("/api/sse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        // 이벤트 발행
        NotificationEvent event = new NotificationEvent(userId, "Test Message");
        sseController.handleNotification(event);

        // Then
        // 전송한 ID가 포함되어있는지 검증
        assertTrue(sseController.getEmitters().containsKey(userId));

    }

    @Test
    @DisplayName("SSE 연결 종료")
    void destroy() throws Exception {
        // Given
        Long userId = 1L;
        // SSE 연결 생성
        mvc.perform(get("/api/sse")
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andDo(print())
                .andExpect(status().isOk());
        // 이벤트 발행
        NotificationEvent event = new NotificationEvent(userId, "Test Message");
        sseController.handleNotification(event);

        // When
        sseController.destroy();

        // Then
        assertFalse(sseController.getEmitters().containsKey(userId));

    }
}