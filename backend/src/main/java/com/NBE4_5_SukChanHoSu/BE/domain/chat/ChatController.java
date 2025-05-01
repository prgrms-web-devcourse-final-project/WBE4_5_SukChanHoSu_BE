package com.NBE4_5_SukChanHoSu.BE.domain.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    @MessageMapping("/chat/message")
    @SendTo("/sub/chat/room")
    public ChatMessage sendMessage(@Payload ChatMessage message) {
        return message;
    }
}
