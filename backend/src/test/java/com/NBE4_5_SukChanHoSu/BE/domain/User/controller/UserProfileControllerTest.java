package com.NBE4_5_SukChanHoSu.BE.domain.User.controller;

import com.NBE4_5_SukChanHoSu.BE.User.controller.UserProfileController;
import com.NBE4_5_SukChanHoSu.BE.User.dto.NicknameCheckResponseDto;
import com.NBE4_5_SukChanHoSu.BE.User.dto.ProfileRequestDto;
import com.NBE4_5_SukChanHoSu.BE.User.dto.ProfileUpdateRequestDto;
import com.NBE4_5_SukChanHoSu.BE.User.dto.UserProfileDto;
import com.NBE4_5_SukChanHoSu.BE.User.service.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileService userProfileService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("닉네임 중복 검사 - 성공")
    void checkNickname() throws Exception {
        String nickname = "testNickname";

        Mockito.when(userProfileService.isNicknameDuplicated(nickname)).thenReturn(false);

        mockMvc.perform(get("/api/profile/check-nickname").param("nickname", nickname)).andExpect(status().isOk()).andExpect(jsonPath("$.code").value("200")).andExpect(jsonPath("$.data.nickname").value(nickname)).andExpect(jsonPath("$.data.duplicated").value(false));
    }

    @Test
    @DisplayName("프로필 등록 - 성공")
    void createProfile() throws Exception {
        ProfileRequestDto request = ProfileRequestDto.builder().nickname("newUser").email("newuser@example.com").gender(null).birthdate(LocalDate.of(1995, 1, 1)).latitude(37.1234).longitude(127.5678).profileImage("https://example.com/profile.png").build();

        // Member 가져오는 부분 rq.getActor()는 실제 테스트에서는 따로 처리하거나 무시해야 함
        // 여기선 간단하게 넘어감 (추가 작업 가능)

        mockMvc.perform(post("/api/profile").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isOk()).andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @DisplayName("프로필 수정 - 성공")
    void updateProfile() throws Exception {
        ProfileUpdateRequestDto updateRequest = ProfileUpdateRequestDto.builder().nickname("updatedNickname").gender(null).profileImage("https://example.com/newprofile.png").latitude(37.5678).longitude(127.1234).birthdate(LocalDate.of(1992, 6, 15)).distance(10).build();

        mockMvc.perform(put("/api/profile").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateRequest))).andExpect(status().isOk()).andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @DisplayName("내 프로필 조회 - 성공")
    void getMyProfile() throws Exception {
        UserProfileDto profile = UserProfileDto.builder().nickname("myNickname").email("myemail@example.com").gender(null).birthdate(LocalDate.of(1990, 5, 20)).latitude(37.0).longitude(127.0).distance(5).build();

        Mockito.when(userProfileService.getMyProfile(Mockito.anyLong())).thenReturn(profile);

        mockMvc.perform(get("/api/profile/me")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value("200")).andExpect(jsonPath("$.data.nickname").value("myNickname")).andExpect(jsonPath("$.data.email").value("myemail@example.com"));
    }
}

