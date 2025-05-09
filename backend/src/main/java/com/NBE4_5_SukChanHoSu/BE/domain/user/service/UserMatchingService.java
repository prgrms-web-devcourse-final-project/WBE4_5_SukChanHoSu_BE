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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMatchingService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final UserLikesRepository userLikesRepository;
    // 사용자별 추천 리스트 관리
    private Map<Long, List<UserProfile>> recommendedUsersMap = new HashMap<>();

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

    // 추천 알고리즘
    public UserProfileResponse recommend(UserProfile userProfile) {
        Long userId = userProfile.getUserId();
        List<UserProfile> recommendedUsers = recommendedUsersMap.getOrDefault(userId, new ArrayList<>());
        int radius = userProfile.getSearchRadius();
        UserProfile recommendedUser = null;
        int maxScore = 0;
        int recommendDistance =0; // 추천 사용자의 거리 저장 필드
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        // 1차: 이성
        List<UserProfile> profileByGender = findProfileByGender(userProfile);

        // 거리 및 태그
        for (UserProfile profile : profileByGender) {
            // 이미 추천한 사용자는 패스
            if (recommendedUsers.contains(profile)) {
                continue;
            }

            LocalDateTime lastLikeTime = userLikesRepository.findLastLikeTimeByUserId(profile.getUserId());
            if (lastLikeTime == null || lastLikeTime.isBefore(oneMonthAgo)) {
                continue;
            }

            int distance = calDistance(userProfile, profile); // 거리 계산
            int distanceScore = 0;
            int tagScore = 0;
            int totalScore = -1;

            // 2차: 거리
            if (distance <= radius) { // 범위 내에 있는 경우만 추가
                distanceScore = 100 - (distance * 3);   // 키로당 3점 감점

                // 3차: 태그
                for(Genre genre : profile.getFavoriteGenres()) {
                    // 나와 태그가 겹친다면
                    if(userProfile.getFavoriteGenres().contains(genre)) {
                        tagScore+=10;   // 하나당 10점
                    }
                }
            }
            totalScore = distanceScore + tagScore; // 총점
            System.out.println("User ID: " + profile.getUserId() + ", Distance: " + distance + ", Distance Score: " + distanceScore + ", Tag Score: " + tagScore + ", Total Score: " + totalScore); // 로깅 추가

            // 최고 점수 사용자 업데이트(0점 이하는 등록x)
            if (totalScore > maxScore) {
                maxScore = totalScore;
                recommendDistance = distance;
                recommendedUser = profile;
            }
        }

        // 추천할 사용자가 있는 경우
        if(recommendedUser != null) {
            recommendedUsers.add(recommendedUser);  // 리스트에 등록
            recommendedUsersMap.put(userId, recommendedUsers); // 사용자별 리스트 업데이트
            System.out.println("리스트관리: "+recommendedUsers);
            return new UserProfileResponse(recommendedUser,recommendDistance);
        }

        // 리스트가 차있는데, 추천할 사용자가 없는 경우, 리스트 초기화
        if (!recommendedUsers.isEmpty()) {
            recommendedUsers.clear(); // 리스트 초기화
            recommendedUsersMap.put(userId, recommendedUsers);  // 초기화 업데이트
        }

        throw new NoRecommendException("404","추천할 사용자가 없습니다.");

    }
}
