package com.NBE4_5_SukChanHoSu.BE.domain.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    private String roomId; //채팅방id
    private String sender; //보낸 사람
    private String message;
}
