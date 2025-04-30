//package com.NBE4_5_SukChanHoSu.BE.domain.user.controller;
//
//import com.NBE4_5_SukChanHoSu.BE.domain.member.dto.request.MemberLoginRequestDto;
//import com.NBE4_5_SukChanHoSu.BE.domain.member.dto.request.MemberSignUpRequestDto;
//import com.NBE4_5_SukChanHoSu.BE.domain.member.entity.Member;
//import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Role;
//import com.NBE4_5_SukChanHoSu.BE.domain.member.repository.MemberRepository;
//import com.NBE4_5_SukChanHoSu.BE.domain.member.service.MemberService;
//import com.NBE4_5_SukChanHoSu.BE.domain.user.controller.UserProfileController;
//
//import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.ProfileRequestDto;
//import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.ProfileUpdateRequestDto;
//import com.NBE4_5_SukChanHoSu.BE.domain.user.enums.Gender;
//import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserProfileService;
//import com.NBE4_5_SukChanHoSu.BE.global.jwt.JwtTokenDto;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.RequestPostProcessor;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//class UserProfileControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private MemberService memberService;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
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
//}
//
