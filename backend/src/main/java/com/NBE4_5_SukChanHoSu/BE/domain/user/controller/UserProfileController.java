package com.NBE4_5_SukChanHoSu.BE.domain.user.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.member.entity.Member;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.ProfileUpdateRequestDto;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.NicknameCheckResponseDto;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.ProfileRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.ProfileResponseDto;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "프로필 관리", description = "프로필 수정 및 닉네임 중복 검사 등 API")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(summary = "프로필 등록", description = "회원가입 후 최초 프로필 등록")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RsData<ProfileResponseDto> createProfile(@AuthenticationPrincipal Member actor, @Valid @RequestBody ProfileRequestDto dto) {
        ProfileResponseDto response = userProfileService.createProfile(actor.getId(), dto);
        return new RsData<>("201", "프로필 등록 완료", response);
    }

    @Operation(summary = "프로필 수정", description = "닉네임, 성별, 위치 등 프로필 정보 수정")
    @PutMapping
    public RsData<ProfileResponseDto> updateProfile(@AuthenticationPrincipal Member actor, @Valid @RequestBody ProfileUpdateRequestDto dto) {
        ProfileResponseDto response = userProfileService.updateProfile(actor.getId(), dto);
        return new RsData<>("200", "프로필 수정 완료", response);
    }

    @Operation(summary = "내 프로필 조회", description = "자신의 프로필 정보 조회")
    @GetMapping("/me")
    public RsData<ProfileResponseDto> getMyProfile(@AuthenticationPrincipal Member actor) {
        ProfileResponseDto response = userProfileService.getMyProfile(actor.getId());
        return new RsData<>("200", "프로필 조회 성공", response);
    }

    @Operation(summary = "닉네임 중복 검사", description = "사용 가능한 닉네임인지 확인합니다.")
    @GetMapping("/check-nickname")
    public RsData<NicknameCheckResponseDto> checkNickname(@Parameter(name = "nickname", description = "중복 검사할 닉네임") @RequestParam String nickname) {
        boolean duplicated = userProfileService.isNicknameDuplicated(nickname);
        return new RsData<>("200", "닉네임 중복 검사 성공", new NicknameCheckResponseDto(nickname, duplicated));
    }


}