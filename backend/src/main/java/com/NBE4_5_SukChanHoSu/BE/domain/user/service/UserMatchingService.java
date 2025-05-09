package com.NBE4_5_SukChanHoSu.BE.domain.user.service;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.UserLikesRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.UserProfileResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserProfileRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import com.NBE4_5_SukChanHoSu.BE.global.exception.user.NoRecommendException;
import com.NBE4_5_SukChanHoSu.BE.global.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMatchingService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final UserLikesRepository userLikesRepository;

    // 사용자별 추천 리스트 관리
//    private Map<Long, List<UserProfile>> recommendedUsersMap = new HashMap<>();
    private Map<Long, List<UserProfile>> recommendedUsersByTagsMap = new HashMap<>();
    private Map<Long, List<UserProfile>> recommendedUsersByDistanceMap = new HashMap<>();

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

    // 거리 내에 존재하는 사용자 리스트
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

    // 범위 내에 존재하는 사용자 추천
    public UserProfileResponse recommendByDistance(UserProfile userProfile, Integer distance) {
        Long userId = userProfile.getUserId();
        List<UserProfile> recommendedUsers = recommendedUsersByDistanceMap.getOrDefault(userId, new ArrayList<>());

        // 범위 이내에 존재하는 사용자 리스트
        List<UserProfileResponse> list = findProfileWithinRadius(userProfile, distance);

        // 이미 추천한 사용자 제외
        List<UserProfileResponse> candidates = list.stream()
                .filter(response -> !recommendedUsers.contains(convertToUserProfile(response)))
                .toList();

        // 남아있는 사용자 있는 경우 랜덤 추천
        if(!candidates.isEmpty()){
            Random random = new Random();
            UserProfileResponse recommendedUser = candidates.get(random.nextInt(candidates.size()));

            // 리스트로 관리
            recommendedUsers.add(convertToUserProfile(recommendedUser));
            recommendedUsersByDistanceMap.put(userId, recommendedUsers);

            return recommendedUser;
        }

        throw new NoRecommendException("404", "추천할 사용자가 없습니다.");
    }

    // response -> profile
    private UserProfile convertToUserProfile(UserProfileResponse response) {
        return UserProfile.builder()
                .userId(response.getUserId())
                .gender(response.getGender())
                .searchRadius(response.getSearchRadius())
                .favoriteGenres(response.getFavoriteGenres())
                .build();
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

    // 태그 기반 매칭
    public UserProfileResponse recommendUserByTags(UserProfile userProfile) {
        long userId = userProfile.getUserId();
        List<UserProfile> recommendedUsers = recommendedUsersByTagsMap.getOrDefault(userId, new ArrayList<>());
        int radius = userProfile.getSearchRadius();
        List<Genre> tags = userProfile.getFavoriteGenres();
        int maxScore = -1;
        int recommendDistance = 0;
        UserProfile recommendedUser = null;

        // 1차: 이성
        List<UserProfile> profileByGender = findProfileByGender(userProfile);

        // 거리 및 태그
        for (UserProfile profile : profileByGender) {
            // 이미 추천한 사용자 pass
            if (recommendedUsers.contains(profile)) continue;

            // 거리 계산
            int distance = calDistance(userProfile, profile);
            // 범위 밖 사용자 패스
            if (distance > radius) continue;

            // 겹치는 태그 계산
            int count = countCommonTags(profile, tags);
            if(count > maxScore) {
                maxScore = count;
                recommendDistance = distance;
                recommendedUser = profile;
            }
        }

        // 추천할 사용자가 있는 경우
        if(recommendedUser != null) {
            recommendedUsers.add(recommendedUser);  // 리스트에 등록
            recommendedUsersByTagsMap.put(userId, recommendedUsers); // 사용자별 리스트 업데이트
            return new UserProfileResponse(recommendedUser,recommendDistance);
        }

        throw new NoRecommendException("404","추천할 사용자가 없습니다.");
    }

    // 두 사용자의 겹치는 태그 수 계산
    private int countCommonTags(UserProfile user, List<Genre> tags) {
        return (int) user.getFavoriteGenres().stream().filter(tags::contains).count();
    }

}
