package com.NBE4_5_SukChanHoSu.BE.domain.user.service;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.ProfileRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.ProfileResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.ProfileUpdateRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.UserProfileResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserProfileRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import com.NBE4_5_SukChanHoSu.BE.global.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProfileResponse createProfile(Long userId, ProfileRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 사용자를 찾을 수 없습니다."));

        // 해당 userId를 가진 UserProfile이 이미 존재하는지 확인
        Optional<UserProfile> existingProfile = userProfileRepository.findByUserId(userId);

        UserProfile userProfile;

        // 프로필이 없으면 새로 생성
        if (!existingProfile.isPresent()) {
            userProfile = new UserProfile();
            userProfile.setUser(user);
            // User 엔티티 연결 (가정: User 엔티티는 이미 존재한다고 가정)
            // User user = userRepository.findById(userId)
            //        .orElseThrow(() -> new RuntimeException("해당 ID의 사용자를 찾을 수 없습니다."));
            // userProfile.setUser(user);
        } else {
            // 프로필이 이미 존재하면 예외 발생 (또는 업데이트 로직 처리 - API 역할에 따라 다름)
            throw new IllegalStateException("이미 프로필이 등록된 사용자입니다.");
        }

        updateEntityFromRequest(userProfile, dto);
        UserProfile savedUserProfile = userProfileRepository.save(userProfile);
        return new ProfileResponse(savedUserProfile); // toDto 대신 생성자 직접 호출
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, ProfileUpdateRequest dto) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId) // 수정: findById -> findByUserId
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // User 엔티티 로딩 (선택 사항)
        // ... (이전 답변에서 설명한 User 엔티티 로딩 로직)

        updateEntityFromUpdateRequest(userProfile, dto);
        UserProfile savedUserProfile = userProfileRepository.save(userProfile);
        return new ProfileResponse(savedUserProfile);
    }

    public boolean isNicknameDuplicated(String nickname) {
        return userProfileRepository.existsByNickName(nickname);
    }

    public ProfileResponse getMyProfile(Long userId) {
        UserProfile userProfile = userProfileRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return new ProfileResponse(userProfile);
    }

    private void updateEntityFromRequest(UserProfile profile, ProfileRequest dto) {
        profile.setNickName(dto.getNickname());
        profile.setGender(dto.getGender());
        profile.setProfileImage(dto.getProfileImage());
        profile.setLatitude(dto.getLatitude());
        profile.setLongitude(dto.getLongitude());
        profile.setBirthdate(dto.getBirthdate());
        profile.setIntroduce(dto.getIntroduce());
        profile.setSearchRadius(dto.getSearchRadius());
        profile.setLifeMovie(dto.getLifeMovie());
        profile.setFavoriteGenres(dto.getFavoriteGenres());
        profile.setWatchedMovies(dto.getWatchedMovies());
        profile.setPreferredTheaters(dto.getPreferredTheaters());
    }

    private void updateEntityFromUpdateRequest(UserProfile profile, ProfileUpdateRequest dto) {
        profile.setNickName(dto.getNickname());
        profile.setGender(dto.getGender());
        profile.setProfileImage(dto.getProfileImage());
        profile.setLatitude(dto.getLatitude());
        profile.setLongitude(dto.getLongitude());
        profile.setBirthdate(dto.getBirthdate());
        profile.setIntroduce(dto.getIntroduce());
        profile.setSearchRadius(dto.getSearchRadius());
        profile.setLifeMovie(dto.getLifeMovie());
        profile.setFavoriteGenres(dto.getFavoriteGenres());
        profile.setWatchedMovies(dto.getWatchedMovies());
        profile.setPreferredTheaters(dto.getPreferredTheaters());
    }

    public UserProfile findUser(Long userId) {
        UserProfile userProfile = userProfileRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("401", "존재하지 않는 유저입니다."));
        return userProfile;
    }

    // 이성만 조회
    public List<UserProfile> findProfileByGender(UserProfile userProfile) {
        return userProfileRepository.findAll().stream().filter(profile -> !profile.getUserId().equals(userProfile.getUserId())) // 자신 제외
                .filter(profile -> !profile.getGender().equals(userProfile.getGender())) // 성별이 다른 유저 필터링
                .toList();
    }

    public List<UserProfileResponse> findProfileWithinRadius(UserProfile userProfile, Integer radius) {
        // 이성으로 필터링
        List<UserProfile> profileByGender = findProfileByGender(userProfile);

        // 거리 계산
        List<UserProfileResponse> responses = new ArrayList<>();
        for (UserProfile profile : profileByGender) {
            int distance = calDistance(userProfile, profile); // 거리 계산
            if (distance <= radius) { // 거리가 범위 이내인 경우만 추가
                responses.add(new UserProfileResponse(profile, distance));
            }
        }
        return responses;
    }

    static final double EARTH_RADIUS = 6371; // 지구의 반지름 (단위: km)

    // 거리 계산
    public int calDistance(UserProfile userProfile1, UserProfile userProfile2) {
        // 내 위/경도
        double lat1 = userProfile1.getLatitude();
        double lon1 = userProfile1.getLongitude();
        // 탐색 대상의 위/경도
        double lat2 = userProfile2.getLatitude();
        double lon2 = userProfile2.getLongitude();

        // 위/경도 차이
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // 곡률 감안해서 어쩌구저쩌구해서 거리 계산
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 거리를 km로 계산
        double distance = EARTH_RADIUS * c;

        // 소수 첫째 자리에서 반올림 후 int형으로 반환
        return (int) Math.round(distance); // 반올림하여 int형으로 반환
    }

    @Transactional
    public void setRadius(UserProfile userProfile, Integer radius) {
        userProfile.setSearchRadius(radius);
    }

    public boolean existsProfileByUserId(Long userId) {
        return userProfileRepository.existsByUserId(userId);
    }
}
