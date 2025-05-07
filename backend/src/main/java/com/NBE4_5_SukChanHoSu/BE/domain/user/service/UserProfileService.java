package com.NBE4_5_SukChanHoSu.BE.domain.user.service;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.UserLikesRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.ProfileRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.ProfileResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.ProfileUpdateRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.UserProfileResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserProfileRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import com.NBE4_5_SukChanHoSu.BE.global.exception.user.NoRecommendException;
import com.NBE4_5_SukChanHoSu.BE.global.exception.user.UserNotFoundException;
import com.NBE4_5_SukChanHoSu.BE.global.util.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final UserLikesRepository userLikesRepository;
    private final S3Util s3Util;

    // 사용자별 추천 리스트 관리
    private Map<Long, List<UserProfile>> recommendedUsersMap = new HashMap<>();


    @Transactional
    public ProfileResponse createProfile(Long userId, ProfileRequest dto, MultipartFile profileImageFile) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("404", "해당 ID의 사용자를 찾을 수 없습니다."));

        Optional<UserProfile> existingProfile = userProfileRepository.findByUserId(userId);

        if (existingProfile.isPresent()) {
            throw new IllegalStateException("이미 프로필이 등록된 사용자입니다.");
        }

        String profileImageUrl = null;
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            profileImageUrl = s3Util.uploadFile(profileImageFile);
        } else if (dto.getProfileImage() != null && !dto.getProfileImage().isEmpty()) {
            profileImageUrl = dto.getProfileImage();
        } else {
            profileImageUrl = "default_profile.png"; // 기본 이미지 URL 설정
            // 필요하다면 기본 이미지 존재 여부 또는 경로 유효성 검사 추가
        }

        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .nickName(dto.getNickname())
                .gender(dto.getGender())
                .profileImage(profileImageUrl)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .birthdate(dto.getBirthdate())
                .introduce(dto.getIntroduce())
                .searchRadius(dto.getSearchRadius())
                .lifeMovie(dto.getLifeMovie())
                .favoriteGenres(dto.getFavoriteGenres())
                .watchedMovies(dto.getWatchedMovies())
                .preferredTheaters(dto.getPreferredTheaters())
                .build();

        UserProfile savedUserProfile = userProfileRepository.save(userProfile);
        return new ProfileResponse(savedUserProfile);
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, ProfileUpdateRequest dto, MultipartFile profileImageFile) throws IOException {
        UserProfile userProfile = userProfileRepository.findByUserId(userId) // 수정: findById -> findByUserId
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String newProfileImageUrl = userProfile.getProfileImage();
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            // 기존 프로필 이미지가 S3에 있다면 삭제
            if (userProfile.getProfileImage() != null && userProfile.getProfileImage().startsWith("https://")) {
                s3Util.deleteFile(userProfile.getProfileImage());
            }
            newProfileImageUrl = s3Util.uploadFile(profileImageFile);
        } else if (dto.getProfileImage() != null && !dto.getProfileImage().isEmpty()) {
            newProfileImageUrl = dto.getProfileImage();
        }

        userProfile = UserProfile.builder()
                .user(userProfile.getUser()) // 기존 User 연결 유지
                .userId(userProfile.getUserId()) // 기존 ID 유지
                .nickName(dto.getNickname())
                .gender(dto.getGender())
                .profileImage(newProfileImageUrl)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .birthdate(dto.getBirthdate())
                .introduce(dto.getIntroduce())
                .searchRadius(dto.getSearchRadius())
                .lifeMovie(dto.getLifeMovie())
                .favoriteGenres(dto.getFavoriteGenres())
                .watchedMovies(dto.getWatchedMovies())
                .preferredTheaters(dto.getPreferredTheaters())
                .build();
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
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
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
    // 태그로 검색
    public List<UserProfileResponse> findProfileByTags(UserProfile userProfile) {
        // 범위 이내에 있는 사용자만 조회
        int radius = userProfile.getSearchRadius();
        List<UserProfileResponse> responses = findProfileWithinRadius(userProfile, radius);

        List<Genre> tags = userProfile.getFavoriteGenres();

        List<UserProfileResponse> filteredResponses = responses.stream()
                .filter(response -> response.getFavoriteGenres().stream()
                        .anyMatch(genre -> tags.stream().anyMatch(genre::equals)))
                .toList();

        return filteredResponses;
    }

    public UserProfileResponse recommend(UserProfile userProfile) {
        Long userId = userProfile.getUserId();
        List<UserProfile> alreadyRecommended = recommendedUsersMap.computeIfAbsent(userId, k -> new ArrayList<>());
        int radius = userProfile.getSearchRadius();
        UserProfile nextRecommendation = null;
        int nextMaxScore = -1;
        int nextRecommendDistance = -1;

        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<UserProfile> profileByGender = findProfileByGender(userProfile);

        // 거리 및 태그
        for (UserProfile profile : profileByGender) {
            if (alreadyRecommended.contains(profile)) {
                continue;
            }

            LocalDateTime lastLikeTime = userLikesRepository.findLastLikeTimeByUserId(profile.getUserId());
            if (lastLikeTime == null || lastLikeTime.isBefore(oneMonthAgo)) {
                continue;
            }

            int distance = calDistance(userProfile, profile);
            int distanceScore = 0;
            int tagScore = 0;
            int totalScore = -1;

            if (distance <= radius) {
                distanceScore = 100 - (distance * 3);
                for (Genre genre : profile.getFavoriteGenres()) {
                    if (userProfile.getFavoriteGenres().contains(genre)) {
                        tagScore += 10;
                    }
                }
            }
            totalScore = distanceScore + tagScore;

            if (totalScore > nextMaxScore) {
                nextMaxScore = totalScore;
                nextRecommendDistance = distance;
                nextRecommendation = profile;
            }
        }

        if (nextRecommendation != null) {
            alreadyRecommended.add(nextRecommendation);
            recommendedUsersMap.put(userId, alreadyRecommended);
            return new UserProfileResponse(nextRecommendation, nextRecommendDistance);
        }

        throw new NoRecommendException("404", "추천할 사용자가 없습니다.");
    }
}
