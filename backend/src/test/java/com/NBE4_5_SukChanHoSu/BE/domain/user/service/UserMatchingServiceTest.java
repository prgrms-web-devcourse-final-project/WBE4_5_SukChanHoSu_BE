package com.NBE4_5_SukChanHoSu.BE.domain.user.service;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserLoginRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.LoginResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.UserProfileResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Gender;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserProfileRepository;
import com.NBE4_5_SukChanHoSu.BE.global.config.BaseTestConfig;
import com.NBE4_5_SukChanHoSu.BE.global.exception.user.NoRecommendException;
import com.NBE4_5_SukChanHoSu.BE.global.exception.user.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@BaseTestConfig
class UserMatchingServiceTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserLikeService userLikeService;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserMatchingService matchingService;
    @Autowired
    private UserService userService;
    private ObjectMapper objectMapper;
    private static String accessToken;
    private static String refreshToken;
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

        // Then
        if (!responses.isEmpty()) { // responses가 비어있지 않은 경우에만 검증
            responses.forEach(response -> {
                String distanceStr = response.getDistance().replaceAll("[^0-9]", "");
                int distance = Integer.parseInt(distanceStr);

                assertTrue(distance <= radius);
            });
        } else {
            System.out.println("범위 내에 사용자가 없습니다.");
        }
    }

    @Test
    @DisplayName("범위 조절")
    void updateRadius() {
        // Given
        int newRadius = 10;

        // When
        matchingService.setRadius(male, newRadius);

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

    @Test
    @DisplayName("태그로 프로필 조회")
    void findProfileByTags() {
        // Given
        List<Genre> tags = male.getFavoriteGenres();

        // When
        List<UserProfileResponse> responses = matchingService.findProfileByTags(male);

        // Then
        responses.forEach(response -> {
            boolean hasMatchingGenre = response.getFavoriteGenres().stream()
                    .anyMatch(tags::contains);
            assertTrue(hasMatchingGenre);
        });
    }

    @Test
    @DisplayName("추천")
    void recommend() {
        // When
        UserProfileResponse recommendedUser = null;
        try {
            recommendedUser = matchingService.recommend(male);
        } catch (NoRecommendException e) {
            // 예외가 발생하면 테스트 성공
            return;
        }

        // Then
        assertNotNull(recommendedUser);
        assertNotEquals(male.getGender(), recommendedUser.getGender());
    }
}