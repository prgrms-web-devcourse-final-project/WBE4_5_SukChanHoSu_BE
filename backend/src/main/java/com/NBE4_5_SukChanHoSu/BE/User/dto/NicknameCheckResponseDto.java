package com.NBE4_5_SukChanHoSu.BE.User.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@AllArgsConstructor
@Schema(description = "닉네임 중복 검사 응답 DTO")
public class NicknameCheckResponseDto {

    @Schema(description = "요청한 닉네임", example = "닉넴1")
    private String nickname;

    @Schema(description = "중복 여부 (true면 이미 사용 중)", example = "false")
    private boolean duplicated;
}



