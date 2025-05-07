package com.NBE4_5_SukChanHoSu.BE.domain.email;

import com.NBE4_5_SukChanHoSu.BE.domain.email.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class EmailControllerTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "authCodeExpirationMillis", 300_000L);
        ReflectionTestUtils.setField(emailService, "senderEmail", "test1@gmail.com");
    }

    @Test
    @DisplayName("인증 코드 생성")
    void createCodeTest() {
        String code = emailService.createCode();
        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("[A-Z0-9]+"));
    }

    @Test
    @DisplayName("이메일 전송 성공")
    void sendSimpleMessageTest() throws MessagingException {
        String testEmail = "test@example.com";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        MimeMessage mockMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);

        String code = emailService.sendSimpleMessage(testEmail);

        assertNotNull(code);
        verify(valueOperations, times(1))
                .set(startsWith("emailAuth:"), eq(code), anyLong(), eq(TimeUnit.MILLISECONDS));
        verify(mailSender, times(1)).send(mockMessage);
    }

    @Test
    @DisplayName("이메일 인증 성공")
    void verifyEmailCodeSuccess() {
        String email = "test@example.com";
        String code = "ABC123";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("emailAuth:" + email)).thenReturn(code);

        boolean result = emailService.verifyEmailCode(email, code);

        assertTrue(result);
        verify(valueOperations).set("emailVerify:" + email, "true", 5, TimeUnit.MINUTES);
        verify(redisTemplate).delete("emailAuth:" + email);
    }

    @Test
    @DisplayName("이메일 인증 실패 - 코드 불일치")
    void verifyEmailCodeFail() {
        String email = "test@example.com";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("emailAuth:" + email)).thenReturn("DIFFERENT");

        boolean result = emailService.verifyEmailCode(email, "WRONG");

        assertFalse(result);
        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any());
    }
}
