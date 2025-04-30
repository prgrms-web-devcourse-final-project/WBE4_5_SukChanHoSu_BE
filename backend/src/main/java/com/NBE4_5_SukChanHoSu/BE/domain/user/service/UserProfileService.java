package com.NBE4_5_SukChanHoSu.BE.domain.user.service;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.ProfileRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.ProfileResponseDto;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.ProfileUpdateRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Transactional
    public ProfileResponseDto createProfile(Long userId, ProfileRequestDto dto) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (userProfile.getNickName() != null) {
            throw new IllegalStateException("이미 프로필이 등록된 사용자입니다.");
        }

        updateEntityFromRequest(userProfile, dto);
        return toDto(userProfileRepository.save(userProfile));
    }

    @Transactional
    public ProfileResponseDto updateProfile(Long userId, ProfileUpdateRequestDto dto) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        updateEntityFromUpdateRequest(userProfile, dto);
        return toDto(userProfileRepository.save(userProfile));
    }

    public boolean isNicknameDuplicated(String nickname) {
        return userProfileRepository.existsByNickName(nickname);
    }

    public ProfileResponseDto getMyProfile(Long userId) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return toDto(userProfile);
    }

    private void updateEntityFromRequest(UserProfile profile, ProfileRequestDto dto) {
        profile.setNickName(dto.getNickname());
        profile.setGender(dto.getGender());
        profile.setProfileImage(dto.getProfileImage());
        profile.setLatitude(dto.getLatitude());
        profile.setLongitude(dto.getLongitude());
        profile.setBirthdate(dto.getBirthdate());
        profile.setIntroduce(dto.getIntroduce());
        // TODO: Favorite genres, lifeMovie, watchedMovies, preferredTheaters, distance 등 매핑 추가
    }

    private void updateEntityFromUpdateRequest(UserProfile profile, ProfileUpdateRequestDto dto) {
        if (dto.getNickname() != null) profile.setNickName(dto.getNickname());
        if (dto.getGender() != null) profile.setGender(dto.getGender());
        if (dto.getProfileImage() != null) profile.setProfileImage(dto.getProfileImage());
        if (dto.getLatitude() != null) profile.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) profile.setLongitude(dto.getLongitude());
        if (dto.getBirthdate() != null) profile.setBirthdate(dto.getBirthdate());
        if (dto.getIntroduce() != null) profile.setIntroduce(dto.getIntroduce());
        // TODO: 생략된 필드 동일하게 적용
    }

    private ProfileResponseDto toDto(UserProfile userProfile) {
        return ProfileResponseDto.builder()
                .nickname(userProfile.getNickName())
                .gender(userProfile.getGender())
                .profileImage(userProfile.getProfileImage())
                .latitude(userProfile.getLatitude())
                .longitude(userProfile.getLongitude())
                .birthdate(userProfile.getBirthdate())
                .introduce(userProfile.getIntroduce())
                .build();
    }
}
