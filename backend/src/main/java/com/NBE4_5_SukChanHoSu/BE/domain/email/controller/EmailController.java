package com.NBE4_5_SukChanHoSu.BE.domain.email.controller;

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
    public RsData<?> mailSend(@RequestParam String email) throws MessagingException {
        try {
            emailService.sendSimpleMessage(email);
            return new RsData<>(
                    "200",
                    "메일 전송 성공"
            );
        } catch (MailException e) {
            return new RsData<>(
                    "400",
                    "메일 전송 실패"
            );
        }
    }

//    // 인증코드 인증
//    @PostMapping("/verify")
//    public String verify(EmailDto emailDto) {
//        log.info("EmailController.verify()");
//        boolean isVerify = emailService.verifyEmailCode(emailDto.getMail(), emailDto.getVerifyCode());
//        return isVerify ? "인증이 완료되었습니다." : "인증 실패하셨습니다.";
//    }
}