package com.NBE4_5_SukChanHoSu.BE.domain.user.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.UserProfileResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserMatchingService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import com.NBE4_5_SukChanHoSu.BE.global.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
@Tag(name = "프로필 관리", description = "프로필 수정 및 닉네임 중복 검사 등 API")
public class UserMatchingController {

    private final UserMatchingService matchingService;

    @Operation(summary = "이성 조회(거리포함)", description = "거리를 포함한 이성 친구만 조회")
    @GetMapping("/gender")
    public RsData<List<UserProfileResponse>> getProfileByGenderWithDistance() {
        User user = SecurityUtil.getCurrentUser();
        Long profileId = user.getUserProfile().getUserId();

        UserProfile userProfile = matchingService.findUser(profileId);
        List<UserProfile> profileByGender = matchingService.findProfileByGender(userProfile);

        List<UserProfileResponse> responses = new ArrayList<>();

        for (UserProfile profile : profileByGender) {
            int distance = matchingService.calDistance(userProfile, profile);
            // UserProfileResponse로 변환하여 리스트에 추가
            responses.add(new UserProfileResponse(profile, distance));
        }

        return new RsData<>("200", "거리 조회 성공", responses);
    }

    @Operation(summary = "매칭 - 거리로 조회", description = "범위 내에 있는 사용자 무작위 조회")
    @GetMapping("/withinRadius")
    public RsData<UserProfileResponse> getProfileWithinRadius() {
        User user = SecurityUtil.getCurrentUser();
        Long profileId = user.getUserProfile().getUserId();

        UserProfile userProfile = matchingService.findUser(profileId);
        int radius = userProfile.getSearchRadius();

        UserProfileResponse response = matchingService.recommendByDistance(userProfile, radius);
        return new RsData<>("200", "거리 조회 성공", response);
    }

    @Operation(summary = "매칭 - 태그로 조회", description = "겹치는 태그가 있는 사람중 매칭 조회")
    @GetMapping("/tags")
    public RsData<UserProfileResponse> recommendByTags() {
        User user = SecurityUtil.getCurrentUser();
        Long profileId = user.getUserProfile().getUserId();

        UserProfile userProfile = matchingService.findUser(profileId);

        UserProfileResponse response = matchingService.recommendUserByTags(userProfile);

        return new RsData<>("200", "프로필 조회 성공", response);
    }

//    @Operation(summary = "추천", description = "유사도가 가장 높은 순서로 추천")
//    @GetMapping("/recommend")
//    public RsData<UserProfileResponse> getRecommend() {
//        User user = SecurityUtil.getCurrentUser();
//        Long profileId = user.getUserProfile().getUserId();
//
//        UserProfile userProfile = matchingService.findUser(profileId);
//        UserProfileResponse response = matchingService.recommend(userProfile);
//
//        return new RsData<>("200", "추천 사용자", response);
//    }


}