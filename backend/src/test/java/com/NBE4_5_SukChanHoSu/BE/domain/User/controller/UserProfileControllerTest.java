//package com.NBE4_5_SukChanHoSu.BE.domain.User.controller;
//
//import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.*;
//import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
//import com.NBE4_5_SukChanHoSu.BE.domain.user.enums.Gender;
//import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserProfileRepository;
//import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserProfileService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//
//import java.util.List;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@WithMockUser
//class UserProfileControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private UserProfileService userProfileService; // @MockBean ❌ @Autowired ⭕️
//
//    @Autowired
//    private UserProfileRepository userProfileRepository;
//
//    @BeforeEach
//    void setUp() {
//        userProfileRepository.deleteAll(); // 또는 특정 사용자 ID에 해당하는 프로필만 삭제
//    }
//
//    @Test
//    @DisplayName("프로필 등록 성공")
//    void createProfile() throws Exception {
//        String json = """
//        {
//          "nickname": "TestUser",
//          "gender": "MALE",
//          "latitude": 37.5665,
//          "longitude": 126.9780,
//          "favoriteGenres": ["ACTION", "COMEDY"],
//          "introduce": "안녕하세요. 영화 좋아하는 사람입니다.",
//          "lifeMovie": "인생 영화는 '인셉션'입니다."
//        }
//        """;
//
//        mockMvc.perform(post("/api/profile")
//                        .param("userId", "1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().isOk());
//    }
//
//
//
//    @Test
//    @DisplayName("프로필 수정 성공")
//    void updateProfile() throws Exception {
//        ProfileUpdateRequestDto updateDto = ProfileUpdateRequestDto.builder()
//                .nickname("UpdatedUser")
//                .gender(Gender.FEMALE)
//                .latitude(37.5777)
//                .longitude(127.0000)
//                .favoriteGenres(List.of(Genre.ACTION.name(), Genre.COMEDY.name()))
//                .build();
//
//        mockMvc.perform(put("/api/profile")
//                        .param("userId", "1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDto)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("닉네임 중복 검사 성공")
//    void checkNickname() throws Exception {
//        mockMvc.perform(get("/api/profile/check-nickname")
//                        .param("nickname", "TestUser"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("내 프로필 조회 성공")
//    void getMyProfile() throws Exception {
//        mockMvc.perform(get("/api/profile/me")
//                        .param("userId", "1"))
//                .andExpect(status().isOk());
//    }
//}
