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
    public void createProfile(Long userId, ProfileRequestDto dto) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (userProfile.getNickName() != null) {
            throw new IllegalStateException("이미 프로필이 등록된 사용자입니다.");
        }

        userProfile.setNickName(dto.getNickname());
        userProfile.setGender(dto.getGender());
        userProfile.setLatitude(dto.getLatitude());
        userProfile.setLongitude(dto.getLongitude());
        userProfile.setBirthdate(dto.getBirthdate());
        userProfile.setProfileImage(dto.getProfileImage());

        userProfileRepository.save(userProfile);
    }

    @Transactional
    public void updateProfile(Long userId, ProfileUpdateRequestDto dto) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (dto.getNickname() != null) userProfile.setNickName(dto.getNickname());
        if (dto.getGender() != null) userProfile.setGender(dto.getGender());
        if (dto.getProfileImage() != null) userProfile.setProfileImage(dto.getProfileImage());
        if (dto.getLatitude() != null) userProfile.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) userProfile.setLongitude(dto.getLongitude());
        if (dto.getBirthdate() != null) userProfile.setBirthdate(dto.getBirthdate());
        if (dto.getIntroduce() != null) userProfile.setIntroduce(dto.getIntroduce());

        userProfileRepository.save(userProfile);
    }

    public boolean isNicknameDuplicated(String nickname) {
        return userProfileRepository.existsByNickName(nickname);
    }

    public ProfileResponseDto getMyProfile(Long userId) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return ProfileResponseDto.builder()
                .nickname(userProfile.getNickName())
                .gender(userProfile.getGender())
                .profileImage(userProfile.getProfileImage())
                .latitude(userProfile.getLatitude())
                .longitude(userProfile.getLongitude())
                .birthdate(userProfile.getBirthdate())
                .introduce(userProfile.getIntroduce())
                // .favoriteGenres(userProfile.getFavoriteGenres())
                .build();
    }
}
