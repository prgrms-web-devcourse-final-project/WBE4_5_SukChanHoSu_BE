package com.NBE4_5_SukChanHoSu.BE.domain.movie.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserLoginRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.responseCode.ReviewErrorCode;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.responseCode.ReviewSuccessCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReviewLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserService userService;

    private String accessToken;

    @BeforeEach
    void setUp() {
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setEmail("initUser1@example.com");
        loginRequest.setPassword("testPassword123!");
        accessToken = userService.login(loginRequest).getAccessToken();
    }

    @Test
    @DisplayName("리뷰 좋아요 - 성공적으로 좋아요 추가")
    void addLikeSuccessFirstTime() throws Exception {
        long reviewId = 1L;

        mockMvc.perform(post("/api/review/like/" + reviewId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ReviewSuccessCode.REVIEW_LIKE_ADD.getCode()))
                .andExpect(jsonPath("$.data.liked", is(true)));
    }

    @Test
    @DisplayName("리뷰 좋아요 - 이미 눌렀던 좋아요 취소")
    void removeLikeSuccess() throws Exception {
        long reviewId = 1L;

        mockMvc.perform(post("/api/review/like/" + reviewId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ReviewSuccessCode.REVIEW_LIKE_CANCEL.getCode()))
                .andExpect(jsonPath("$.data.liked", is(false)));
    }

    @Test
    @DisplayName("리뷰 좋아요 - 리뷰가 존재하지 않을 때 예외 발생")
    void addLikeReviewNotFound() throws Exception {
        long reviewId = 99999L;

        mockMvc.perform(post("/api/review/like/" + reviewId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ReviewErrorCode.REVIEW_NOT_FOUND.getCode()));
    }
}
