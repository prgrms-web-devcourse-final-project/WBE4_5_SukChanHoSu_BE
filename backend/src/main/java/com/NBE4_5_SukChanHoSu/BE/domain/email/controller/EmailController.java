package com.NBE4_5_SukChanHoSu.BE.domain.email.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.email.dto.request.EmailDto;
import com.NBE4_5_SukChanHoSu.BE.domain.email.service.EmailService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/send")
    public RsData<String> mailSend(@RequestParam String email) throws MessagingException {
        try {
            String authCode = emailService.sendSimpleMessage(email);
            return new RsData<>(
                    "200",
                    "메일 전송 성공",
                    authCode
            );
        } catch (MailException e) {
            return new RsData<>(
                    "400",
                    "메일 전송 실패"
            );
        }
    }

    @PostMapping("/verify")
    public RsData<?> verify(EmailDto emailDto) {
        boolean isVerify = emailService.verifyEmailCode(emailDto.getMail(), emailDto.getVerifyCode());
        if (isVerify) {
            return new RsData<>(
                    "200",
                    "인증이 완료되었습니다."
            );
        }
        return new RsData<>(
                "400",
                "인증이 실패했습니다."
        );
    }
}