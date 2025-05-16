package com.NBE4_5_SukChanHoSu.BE.domain.chat.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.chat.dto.ChatMessage;
import com.NBE4_5_SukChanHoSu.BE.domain.chat.service.ChatMessageService;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserErrorCode;
import com.NBE4_5_SukChanHoSu.BE.global.exception.security.BadCredentialsException;
import com.NBE4_5_SukChanHoSu.BE.global.security.PrincipalDetails;
import com.NBE4_5_SukChanHoSu.BE.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;


@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat/message")
    public void sendMessage(@Payload ChatMessage message, Principal principal) {

        if (!(principal instanceof UsernamePasswordAuthenticationToken token)) {
            throw new BadCredentialsException(
                    UserErrorCode.USER_UNAUTHORIZED.getCode(),
                    UserErrorCode.USER_UNAUTHORIZED.getMessage()
            );
        }

        Object principalObj = token.getPrincipal();
        if (!(principalObj instanceof PrincipalDetails details)) {
            throw new BadCredentialsException(
                    UserErrorCode.USER_UNAUTHORIZED.getCode(),
                    UserErrorCode.USER_UNAUTHORIZED.getMessage()
            );
        }


        String nickname = details.getUser().getUserProfile().getNickName(); // or getUser().getNickname() if 존재
        message.setSender(nickname);

        message.setSentAt(LocalDateTime.now());

        // Redis에 메시지 저장
        chatMessageService.saveMessage(message);

        //메시지 전송
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);


    }
}
