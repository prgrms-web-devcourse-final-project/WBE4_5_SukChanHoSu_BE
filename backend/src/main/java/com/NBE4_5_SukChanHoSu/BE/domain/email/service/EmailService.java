package com.NBE4_5_SukChanHoSu.BE.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String EMAIL_AUTH = "emailAuth:";
    private static final String EMAIL_VERIFY = "emailVerify:";
    private static final String TRUE = "true";

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${spring.mail.properties.auth-code-expiration-millis}")
    private Long authCodeExpirationMillis;

    public String createCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(2);

            switch (index) {
                case 0 -> key.append((char) (random.nextInt(26) + 65));
                case 1 -> key.append(random.nextInt(10));
            }
        }
        return key.toString();
    }

    public MimeMessage createMail(String mail, String authCode) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("이메일 인증");
        String body = "";
        body += "<h3>요청하신 인증 번호입니다.</h3>";
        body += "<h1>" + authCode + "</h1>";
        body += "<h3>감사합니다.</h3>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    public String sendSimpleMessage(String sendEmail) throws MessagingException {
        String authCode = createCode();
        String key = EMAIL_AUTH + sendEmail;
        redisTemplate.opsForValue().set(key, authCode, authCodeExpirationMillis, TimeUnit.MILLISECONDS);

        MimeMessage message = createMail(sendEmail, authCode);
        javaMailSender.send(message);

        return authCode;
    }

    public boolean verifyEmailCode(String email, String verifyCode) {
        String key = EMAIL_AUTH + email;
        String authCode = redisTemplate.opsForValue().get(key);
        if (authCode != null && authCode.equals(verifyCode)) {
            redisTemplate.opsForValue().set(EMAIL_VERIFY + email, TRUE, 5, TimeUnit.MINUTES);
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
}
