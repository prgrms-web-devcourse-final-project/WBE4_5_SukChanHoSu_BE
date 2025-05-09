package com.NBE4_5_SukChanHoSu.BE.domain.user.service;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.UserProfileResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Gender;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserProfileRepository;
import com.NBE4_5_SukChanHoSu.BE.global.config.BaseTestConfig;
import com.NBE4_5_SukChanHoSu.BE.global.exception.user.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@BaseTestConfig
class UserMatchingServiceTest {
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserMatchingService matchingService;
    @Autowired
    private UserProfileService userProfileService;

    private ObjectMapper objectMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private UserProfile male;
    private UserProfile female;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        login();
    }

    @DisplayName("로그인")
    void login() {
        male = userProfileRepository.findByGender(Gender.Male)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("남자 사용자가 없습니다."));

        female = userProfileRepository.findByGender(Gender.Female)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("여자 사용자가 없습니다."));
    }

    @Test
    @DisplayName("범위 내에 존재하는 사용자 조회")
    void getUserWithinRadius() throws Exception {

        // Given
        int radius = male.getSearchRadius();
        System.out.println("radius: "+ radius);

        // When
        List<UserProfileResponse> responses = matchingService.findProfileWithinRadius(male, radius);
        if (!responses.isEmpty()) { // responses가 비어있지 않은 경우에만 검증
            responses.forEach(response -> {
                String distanceStr = response.getDistance();
                int distanceValue = extractDistanceValue(distanceStr);

                assertTrue(distanceValue <= radius);
            });
        } else{
            System.out.println("범위 내에 사용자가 없습니다.");
        }
    }

    // 거리 문자열에서 숫자만 추출하여 반환 (예: "약 1km" -> 1)
    private int extractDistanceValue(String distance) {
        String distanceNumber = distance.replaceAll("[^0-9]", "");
        return Integer.parseInt(distanceNumber);
    }

    @Test
    @DisplayName("범위 조절")
    void updateRadius() {
        // Given
        int newRadius = 10;

        // When
        userProfileService.setRadius(male, newRadius);

        // Then
        UserProfile updatedUser = userProfileRepository.findById(male.getUserId())
                .orElseThrow(() -> new UserNotFoundException("401", "존재하지 않는 유저입니다."));
        assertEquals(newRadius, updatedUser.getSearchRadius());
    }

    @Test
    @DisplayName("이성 조회(거리 포함)")
    void findProfileByGender() {
        // When
        List<UserProfile> profiles = matchingService.findProfileByGender(male);

        // Then
        profiles.forEach(profile -> assertNotEquals(male.getGender(), profile.getGender()));
    }

}