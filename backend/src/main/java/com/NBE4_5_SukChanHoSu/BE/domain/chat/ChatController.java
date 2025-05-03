package com.NBE4_5_SukChanHoSu.BE.domain.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/chat/message")
    public void sendMessage(@Payload ChatMessage message) {
        // roomId 기반으로 전송.
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }


}
