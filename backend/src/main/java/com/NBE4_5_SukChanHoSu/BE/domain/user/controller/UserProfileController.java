package com.NBE4_5_SukChanHoSu.BE.domain.user.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.ProfileUpdateRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.NicknameCheckResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.ProfileRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.ProfileResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "프로필 관리", description = "프로필 수정 및 닉네임 중복 검사 등 API")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserService userService;

    @Operation(summary = "프로필 등록", description = "회원가입 후 최초 프로필 등록")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RsData<ProfileResponse> createProfile(@AuthenticationPrincipal User actor, @Valid @RequestBody ProfileRequest dto) {
        ProfileResponse response = userProfileService.createProfile(actor.getId(), dto);
        return new RsData<>("201", "프로필 등록 완료", response);
    }

    @Operation(summary = "프로필 수정", description = "닉네임, 성별, 위치 등 프로필 정보 수정")
    @PutMapping
    public RsData<ProfileResponse> updateProfile(@AuthenticationPrincipal User actor, @Valid @RequestBody ProfileUpdateRequest dto) {
        ProfileResponse response = userProfileService.updateProfile(actor.getId(), dto);
        return new RsData<>("200", "프로필 수정 완료", response);
    }

    @Operation(summary = "내 프로필 조회", description = "자신의 프로필 정보 조회")
    @GetMapping("/me")
    public RsData<ProfileResponse> getMyProfile(@AuthenticationPrincipal User actor) {
        ProfileResponse response = userProfileService.getMyProfile(actor.getId());
        return new RsData<>("200", "프로필 조회 성공", response);
    }

    @Operation(summary = "닉네임 중복 검사", description = "사용 가능한 닉네임인지 확인합니다.")
    @GetMapping("/check-nickname")
    public RsData<NicknameCheckResponse> checkNickname(@Parameter(name = "nickname", description = "중복 검사할 닉네임") @RequestParam String nickname) {
        boolean duplicated = userProfileService.isNicknameDuplicated(nickname);
        return new RsData<>("200", "닉네임 중복 검사 성공", new NicknameCheckResponse(nickname, duplicated));
    }

    @Operation(summary = "프로필로 유저 객체 조회", description = "프로필 엔티티를 이용하여 유저 정보 가져오는지 확인")
    @GetMapping("/user")
    // todo: 프로바이더 제공되면 파라미터에서 헤더로 변경
    public RsData<User> getUser(@RequestParam Long profileId) {
        UserProfile profile = userProfileService.findUser(profileId);
        return new RsData<>("200", "프로필 조회 성공", profile.getUser());
    }

    @Operation(summary = "유저로 프로필 객체 조회", description = "유저 엔티티를 이용하여 프로필 정보 가져오는지 확인")
    @GetMapping("/userProfile")
    // todo: 프로바이더 제공되면 파라미터에서 헤더로 변경
    public RsData<UserProfile> getProfile(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        return new RsData<>("200", "프로필 조회 성공", user.getUserProfile());
    }

}