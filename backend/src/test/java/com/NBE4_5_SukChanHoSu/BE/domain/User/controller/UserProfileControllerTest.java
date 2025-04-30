package com.NBE4_5_SukChanHoSu.BE.domain.user.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserLoginRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserSignUpRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserProfileService;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserService;
import com.NBE4_5_SukChanHoSu.BE.global.jwt.JwtTokenDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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
        String email = "test@example.com";
        String rawPassword = "testPassword123!";

        // 회원가입
        UserSignUpRequest signUpDto = new UserSignUpRequest();
        signUpDto.setEmail(email);
        signUpDto.setPassword(rawPassword);
        signUpDto.setPasswordConfirm(rawPassword);
        userService.join(signUpDto);

        // 로그인
        UserLoginRequest loginDto = new UserLoginRequest();
        loginDto.setEmail(email);
        loginDto.setPassword(rawPassword);

        // when
        JwtTokenDto tokenDto = userService.login(loginDto);
        this.jwtToken = tokenDto.getAccessToken();
    }


}

