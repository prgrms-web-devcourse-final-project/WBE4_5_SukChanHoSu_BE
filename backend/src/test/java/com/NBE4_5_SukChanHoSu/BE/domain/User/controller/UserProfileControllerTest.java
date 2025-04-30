package com.NBE4_5_SukChanHoSu.BE.domain.user.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserLoginRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserSignUpRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserProfileService;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserService;
import com.NBE4_5_SukChanHoSu.BE.global.config.BaseTestConfig;
import com.NBE4_5_SukChanHoSu.BE.global.jwt.JwtTokenDto;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@BaseTestConfig
class UserProfileControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ObjectMapper objectMapper;

//    // 회원 가입 + 로그인 후 JWT 토큰 얻기
//    private String createToken(String email) {
//        memberService.join(new MemberSignUpRequestDto(email, "1234", "1234"));
//        JwtTokenDto jwt = memberService.login(new MemberLoginRequestDto(email, "1234"));
//        return jwt.getAccessToken();
//    }
//
//    @Test
//    void 내_프로필_조회_성공() throws Exception {
//        String token = createToken("user1@example.com");
//
//        // 우회: SecurityContext 강제 설정
//        Member member = memberRepository.findByEmail("user1@example.com").orElseThrow();
//        CustomUserDetails userDetails = new CustomUserDetails(member);
//        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(auth);
//
//        mockMvc.perform(get("/api/profile/me")
//                        .header("Authorization", "Bearer " + token))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value("200"));
//    }
//
//    @Test
//    void 프로필_등록_성공() throws Exception {
//        String token = createToken("user2@example.com");
//
//        ProfileRequestDto dto = ProfileRequestDto.builder()
//                .nickname("testnick")
//                .gender(Gender.FEMALE)
//                .introduce("자기소개입니다.")
//                .build();
//        String body = objectMapper.writeValueAsString(dto);
//
//        mockMvc.perform(post("/api/profile")
//                        .header("Authorization", "Bearer " + token)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(body))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.code").value("201"));
//    }

    private String jwtToken;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        login();
    }

    @DisplayName("로그인")
    void login() {
        // given
        String email = "testuser1@example.com";
        String rawPassword = "testPassword123!";

        // 로그인
        UserLoginRequest loginDto = new UserLoginRequest();
        loginDto.setEmail(email);
        loginDto.setPassword(rawPassword);

        // when
        JwtTokenDto tokenDto = userService.login(loginDto);
        this.jwtToken = tokenDto.getAccessToken();
    }

    @Test
    @DisplayName("범위 내에 존재하는 사용자 조회")
    void getUserWithinRadius() throws Exception {
        //given
        UserProfile userProfile = userProfileService.findUser(1L);
        int radius = userProfile.getSearchRadius();

        //when
        ResultActions action = mvc.perform(get("/api/profile/withinRadius")
                        .param("profileId","1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andDo(print());
        String responseBody = action.andReturn().getResponse().getContentAsString();
        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONArray usersArray = jsonResponse.getJSONArray("data");

        // then
        action.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message",containsString("성공")));

        for (int i = 0; i < usersArray.length(); i++) {
            JSONObject user = usersArray.getJSONObject(i);
            String distanceStr = user.getString("distance");

            // 거리 값에서 숫자만 추출
            int distanceValue = extractDistanceValue(distanceStr);

            // 반경(radius) 이하인지 확인
            assertTrue(distanceValue <= radius);

        }

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
                        .param("profileId","1")
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
                        .param("profileId","1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                        .andExpect(jsonPath("$.code").value("200"))
                        .andExpect(jsonPath("$.message",containsString("성공")))
                        .andExpect(jsonPath("$.data.searchRadius").value(radius));
    }

    @Test
    @DisplayName("이성 조회(거리 포함)")
    void findProfileByGender() throws Exception {
        // given

        // when
        ResultActions action = mvc.perform(get("/api/profile/profiles/gender")
                        .param("profileId","1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andDo(print());

        // then
        action.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message",containsString("성공")))
                .andExpect(jsonPath("$.data[*].distance").exists());

    }

}

