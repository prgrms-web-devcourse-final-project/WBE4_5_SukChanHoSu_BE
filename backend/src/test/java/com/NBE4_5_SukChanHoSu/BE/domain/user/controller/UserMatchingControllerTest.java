package com.NBE4_5_SukChanHoSu.BE.domain.user.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserLoginRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.LoginResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserMatchingService;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserProfileService;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserService;
import com.NBE4_5_SukChanHoSu.BE.global.config.BaseTestConfig;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@BaseTestConfig
class UserMatchingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserMatchingService matchingService;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        login();
    }

    @DisplayName("로그인")
    void login() {
        // given
        String email = "initUser1@example.com";
        String rawPassword = "testPassword123!";

        // 로그인
        UserLoginRequest loginDto = new UserLoginRequest();
        loginDto.setEmail(email);
        loginDto.setPassword(rawPassword);

        // when
        LoginResponse tokenDto = userService.login(loginDto);
        this.jwtToken = tokenDto.getAccessToken();
    }

    @Test
    @DisplayName("추천 - 거리")
    void getUserWithinRadius() throws Exception {
        //given
        UserProfile userProfile = matchingService.findUser(1L);
        int radius = userProfile.getSearchRadius();

        //when
        ResultActions action = mvc.perform(get("/api/matching/withinRadius")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andDo(print());

        // 파싱
        String responseBody = action.andReturn().getResponse().getContentAsString();
        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONObject user = jsonResponse.getJSONObject("data");

        // then
        action.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message",containsString("성공")));

        String distanceStr = user.getString("distance");

        // 거리 값에서 숫자만 추출
        int distanceValue = extractDistanceValue(distanceStr);

        // 반경(radius) 이하인지 확인
        assertTrue(distanceValue <= radius);

    }

    // 거리 문자열에서 숫자만 추출하여 반환 (예: "약 1km" -> 1)
    private int extractDistanceValue(String distance) {
        String distanceNumber = distance.replaceAll("[^0-9]", "");
        return Integer.parseInt(distanceNumber);
    }

    @Test
    @DisplayName("범위 조절")
    void updateRadius() throws Exception {
        // given
        int radius = 10;

        // when
        ResultActions action = mvc.perform(put("/api/profile/radius")
                        .param("radius", String.valueOf(radius))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andDo(print());

        // then
        action.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message",containsString("성공")))
                .andExpect(jsonPath("$.data.searchRadius").value(radius));

        mvc.perform(get("/api/profile/profile/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message",containsString("성공")))
                .andExpect(jsonPath("$.data.searchRadius").value(radius));
    }

    @Test
    @DisplayName("추천 - 태그")
    void recommendByTags() throws Exception {
        // when
        // 추천 1
        ResultActions action1 = mvc.perform(get("/api/matching/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andDo(print());
        // 응답 파싱
        int status1 = action1.andReturn().getResponse().getStatus();
        String responseBody1 = action1.andReturn().getResponse().getContentAsString();
        JSONObject jsonResponse1 = new JSONObject(responseBody1);

        // 추천 2
        ResultActions action2 = mvc.perform(get("/api/matching/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andDo(print());
        // 응답 파싱
        int status2 = action2.andReturn().getResponse().getStatus();
        String responseBody2 = action2.andReturn().getResponse().getContentAsString();
        JSONObject jsonResponse2 = new JSONObject(responseBody2);

        // Then
        // 404 체크
        if (status1 == HttpStatus.NOT_FOUND.value()) {
            assertEquals("404", jsonResponse1.getString("code"));
            assertEquals("추천할 사용자가 없습니다.", jsonResponse1.getString("message"));
        } else if (status2 == HttpStatus.NOT_FOUND.value()) {
            assertEquals("404", jsonResponse2.getString("code"));
            assertEquals("추천할 사용자가 없습니다.", jsonResponse2.getString("message"));
        } else{
            // 둘다 200 OK를 반환한 경우 ->  응답이 달라야함
            JSONObject user1 = jsonResponse1.getJSONObject("data");
            JSONObject user2 = jsonResponse2.getJSONObject("data");

            assertNotEquals(user1.toString(), user2.toString());
        }
    }


}
