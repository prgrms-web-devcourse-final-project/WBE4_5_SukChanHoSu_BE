package com.NBE4_5_SukChanHoSu.BE.domain.email.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.email.dto.request.EmailDto;
import com.NBE4_5_SukChanHoSu.BE.domain.email.service.EmailService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
@Tag(name = "이메일 인증", description = "이메일 전송 및 인증 코드 검증 API")
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/send")
    @Operation(
            summary = "인증 메일 전송",
            description = "입력한 이메일 주소로 인증 코드를 포함한 메일을 전송합니다."
    )
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
    @Operation(
            summary = "이메일 인증 코드 검증",
            description = "입력한 이메일과 인증 코드를 검증하여 인증 여부를 반환합니다."
    )
    public RsData<?> verify(@RequestBody EmailDto emailDto) {
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
