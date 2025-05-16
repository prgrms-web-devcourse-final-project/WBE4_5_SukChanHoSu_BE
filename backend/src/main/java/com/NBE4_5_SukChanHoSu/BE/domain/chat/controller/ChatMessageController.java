package com.NBE4_5_SukChanHoSu.BE.domain.chat.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.chat.dto.ChatMessage;
import com.NBE4_5_SukChanHoSu.BE.domain.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/rooms")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    //채팅 메시지 내역 조회
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable String roomId) {
        List<ChatMessage> messages = chatMessageService.getMessageHistory(roomId);
        return ResponseEntity.ok(messages);
    }
}
