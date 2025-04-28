package com.NBE4_5_SukChanHoSu.BE.User.controller;

import com.NBE4_5_SukChanHoSu.BE.User.dto.ProfileUpdateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.NBE4_5_SukChanHoSu.BE.User.dto.NicknameCheckResponseDto;
import com.NBE4_5_SukChanHoSu.BE.User.dto.ProfileRequestDto;
import com.NBE4_5_SukChanHoSu.BE.User.dto.UserProfileDto;
import com.NBE4_5_SukChanHoSu.BE.User.service.UserProfileService;
import com.NBE4_5_SukChanHoSu.BE.global.RsData;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "프로필 관리", description = "프로필 수정 및 닉네임 중복 검사 등 API")
public class UserProfileController {

    private final UserProfileService userProfileService;

//    @Operation(summary = "프로필 등록", description = "회원가입 후 최초 프로필 등록")
//    @PostMapping
//    public RsData<ProfileRequestDto> createProfile(@Valid @RequestBody ProfileRequestDto dto) {
//        Member actor = rq.getActor();
//        userProfileService.createProfile(actor.getId(), dto);
//        return new RsData<>("200", "프로필 등록 완료");
//    }
//
//    @Operation(summary = "프로필 수정", description = "사용자의 닉네임, 이메일, 성별, 위치 등 프로필을 수정합니다.")
//    @PutMapping
//    public RsData<UserProfileDto> updateProfile(@Valid @RequestBody UserProfileDto dto) {
//        Member actor = rq.getActor();
//        userProfileService.updateProfile(actor.getId(), dto);
//        return new RsData<>("200", "프로필 수정 완료", dto);
//    }
//
//    @Operation(summary = "닉네임 중복 검사", description = "사용 가능한 닉네임인지 확인합니다.")
//    @GetMapping("/check-nickname")
//    public RsData<NicknameCheckResponseDto> checkNickname(@RequestParam String nickname) {
//        boolean duplicated = userProfileService.isNicknameDuplicated(nickname);
//        return new RsData<>("200", "닉네임 중복 검사 성공", new NicknameCheckResponseDto(nickname, duplicated));
//    }
//
//    @Operation(summary = "내 프로필 조회", description = "자신의 프로필 정보를 조회합니다.")
//    @GetMapping("/me")
//    public RsData<UserProfileDto> getMyProfile() {
//        Member actor = rq.getActor();
//        UserProfileDto profile = userProfileService.getMyProfile(actor.getId());
//        return new RsData<>("200", "프로필 조회 성공", profile);
//    }

    @Operation(summary = "프로필 등록", description = "회원가입 후 최초 프로필 등록")
    @PostMapping
    public RsData<ProfileRequestDto> createProfile(@RequestParam Long userId, @Valid @RequestBody ProfileRequestDto dto) {

        userProfileService.createProfile(userId, dto);
        return new RsData<>("200", "프로필 등록 완료");
    }

    @Operation(summary = "프로필 수정", description = "사용자의 닉네임, 이메일, 성별, 위치 등 프로필을 수정합니다.")
    @PutMapping
    public RsData<ProfileUpdateRequestDto> updateProfile(@RequestParam Long userId, @Valid @RequestBody ProfileUpdateRequestDto dto) {
        userProfileService.updateProfile(userId, dto);
        return new RsData<>("200", "프로필 수정 완료", dto);
    }

    @Operation(summary = "닉네임 중복 검사", description = "사용 가능한 닉네임인지 확인합니다.")
    @GetMapping("/check-nickname")
    public RsData<NicknameCheckResponseDto> checkNickname(@RequestParam String nickname) {
        boolean duplicated = userProfileService.isNicknameDuplicated(nickname);
        return new RsData<>("200", "닉네임 중복 검사 성공", new NicknameCheckResponseDto(nickname, duplicated));
    }

    @Operation(summary = "내 프로필 조회", description = "자신의 프로필 정보를 조회합니다.")
    @GetMapping("/me")
    public RsData<UserProfileDto> getMyProfile(@RequestParam Long userId) {
        UserProfileDto profile = userProfileService.getMyProfile(userId);
        return new RsData<>("200", "프로필 조회 성공", profile);
    }

}

