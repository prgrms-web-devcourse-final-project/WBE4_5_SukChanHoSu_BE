package com.NBE4_5_SukChanHoSu.BE.domain.user.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.UserProfileResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.ProfileUpdateRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import com.NBE4_5_SukChanHoSu.BE.global.util.SecurityUtil;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public RsData<ProfileResponse> createProfile(@Valid @RequestBody ProfileRequest dto) {
        ProfileResponse response = userProfileService.createProfile(SecurityUtil.getCurrentUserId(), dto);
        return new RsData<>("201", "프로필 등록 완료", response);
    }

    @Operation(summary = "프로필 수정", description = "닉네임, 성별, 위치 등 프로필 정보 수정")
    @PutMapping
    public RsData<ProfileResponse> updateProfile(@Valid @RequestBody ProfileUpdateRequest dto) {
        ProfileResponse response = userProfileService.updateProfile(SecurityUtil.getCurrentUserId(), dto);
        return new RsData<>("200", "프로필 수정 완료", response);
    }

    @Operation(summary = "내 프로필 조회", description = "자신의 프로필 정보 조회")
    @GetMapping("/me")
    public RsData<ProfileResponse> getMyProfile() {
        ProfileResponse response = userProfileService.getMyProfile(SecurityUtil.getCurrentUserId());
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

    @Operation(summary = "범위 조절", description = "탐색 범위 조절")
    @PutMapping("/radius")
    public RsData<?> setRadius(@RequestParam Long profileId, @RequestParam Integer radius) {
        UserProfile userProfile = userProfileService.findUser(profileId);
        userProfileService.setRadius(userProfile, radius);

        return new RsData<>("200", "프로필 조회 성공", userProfile);
    }

    @Operation(summary = "이성 조회(거리포함)", description = "거리를 포함한 이성 친구만 조회")
    @GetMapping("/profiles/gender")
    public RsData<List<UserProfileResponse>> getProfileByGenderWithDistance(@RequestParam Long profileId) {
        UserProfile userProfile = userProfileService.findUser(profileId);
        List<UserProfile> profileByGender = userProfileService.findProfileByGender(userProfile);

        List<UserProfileResponse> responses = new ArrayList<>();

        for (UserProfile profile : profileByGender) {
            int distance = userProfileService.calDistance(userProfile, profile);
            // UserProfileResponse로 변환하여 리스트에 추가
            responses.add(new UserProfileResponse(profile, distance));
        }

        return new RsData<>("200", "거리 조회 성공", responses);
    }

    @Operation(summary = "범위 이내 사용자 조회", description = "범위 내에 있는 사용자만 조회")
    @GetMapping("/withinRadius")
    public RsData<List<UserProfileResponse>> getProfileWithinRadius(@RequestParam Long profileId) {
        UserProfile userProfile = userProfileService.findUser(profileId);
        int radius = userProfile.getSearchRadius();

        List<UserProfileResponse> responses = userProfileService.findProfileWithinRadius(userProfile, radius);
        return new RsData<>("200", "거리 조회 성공", responses);
    }

    @Operation(summary = "내 프로필 조회", description = "자신의 프로필 정보 조회")
    @GetMapping("/profile/me")
    //todo 임시, 이후 삭제
    public RsData<UserProfile> getMyProfile(@RequestParam Long profileId) {
        UserProfile userProfile = userProfileService.findUser(profileId);
        return new RsData<>("200", "프로필 조회 성공", userProfile);
    }
}