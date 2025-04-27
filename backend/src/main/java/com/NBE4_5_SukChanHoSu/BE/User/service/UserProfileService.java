package com.NBE4_5_SukChanHoSu.BE.User.service;

import lombok.RequiredArgsConstructor;
import com.NBE4_5_SukChanHoSu.BE.User.dto.ProfileRequestDto;
import com.NBE4_5_SukChanHoSu.BE.User.dto.UserProfileDto;
import com.NBE4_5_SukChanHoSu.BE.User.entity.User;
import com.NBE4_5_SukChanHoSu.BE.User.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    public void createProfile(Long userId, ProfileRequestDto dto) {
        User user = userRepository.findById(userId).orElseThrow();

        if (user.getNickname() != null) {
            throw new IllegalStateException("이미 프로필이 등록된 사용자입니다.");
        }

        user.setNickname(dto.getNickname());
        user.setGender(dto.getGender());
        user.setLatitude(dto.getLatitude());
        user.setLongitude(dto.getLongitude());
        user.setBirthdate(dto.getBirthdate());
        user.setEmail(dto.getEmail());
        user.setProfileImage(dto.getProfileImage());

        userRepository.save(user);
    }

    public void updateProfile(Long userId, UserProfileDto dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 없음"));

        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setGender(dto.getGender());
        user.setProfileImage(dto.getProfileImage());
        user.setLatitude(dto.getLatitude());
        user.setLongitude(dto.getLongitude());
        user.setBirthdate(dto.getBirthdate());

        userRepository.save(user);
    }

    public boolean isNicknameDuplicated(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public UserProfileDto getMyProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 없음"));

        return UserProfileDto.builder().nickname(user.getNickname()).email(user.getEmail()).gender(user.getGender()).profileImage(user.getProfileImage()).latitude(user.getLatitude()).longitude(user.getLongitude()).birthdate(user.getBirthdate()).build();
    }
}

