package com.NBE4_5_SukChanHoSu.BE.domain.movie.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.request.ReviewCreateDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.request.ReviewUpdateDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.responseCode.ReviewSuccessCode;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.service.ReviewService;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserLoginRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.closeTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;
    private String accessToken;

    @BeforeEach
    void setUp() {
        // 로그인하여 accessToken 발급
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("initUser1@example.com");
        request.setPassword("testPassword123!");
        accessToken = userService.login(request).getAccessToken();
    }

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReviewSuccess() throws Exception {
        ReviewCreateDto requestDto = new ReviewCreateDto();
        requestDto.setMovieId(20070001L);
        requestDto.setContent("Amazing movie!");
        requestDto.setRating(5.0);

        mockMvc.perform(post("/api/movie/review")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ReviewSuccessCode.REVIEW_CREATED.getCode()))
                .andExpect(jsonPath("$.message").value(ReviewSuccessCode.REVIEW_CREATED.getMessage()))
                .andExpect(jsonPath("$.data.title").value("Inception"));
    }

    @Test
    @DisplayName("리뷰 조회 성공")
    void getReviewByIdSuccess() throws Exception {
        // given
        Long reviewId = 1L;

        // when
        ResultActions action = mockMvc.perform(get("/api/movie/review/{id}", reviewId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        action.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ReviewSuccessCode.REVIEW_FETCHED.getCode()))
                .andExpect(jsonPath("$.message").value(ReviewSuccessCode.REVIEW_FETCHED.getMessage()))
                .andExpect(jsonPath("$.data.title").value("Inception"));
    }


    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReviewSuccess() throws Exception {
        Long reviewId = 5L;

        ReviewUpdateDto updateDto = new ReviewUpdateDto();
        updateDto.setContent("is good");
        updateDto.setRating(4.0);

        mockMvc.perform(patch("/api/movie/review/{id}", reviewId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ReviewSuccessCode.REVIEW_UPDATED.getCode()))
                .andExpect(jsonPath("$.message").value(ReviewSuccessCode.REVIEW_UPDATED.getMessage()))
                .andExpect(jsonPath("$.data.content").value("is good"))
                .andExpect(jsonPath("$.data.rating").value(4.0));
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReviewSuccess() throws Exception {
        Long reviewId = 1L;

        mockMvc.perform(delete("/api/movie/review/{id}", reviewId)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ReviewSuccessCode.REVIEW_DELETED.getCode()))
                .andExpect(jsonPath("$.message").value(ReviewSuccessCode.REVIEW_DELETED.getMessage()));
    }

    @Test
    @DisplayName("리뷰 목록 조회 - 최신순 정렬")
    void getAllReviewsSortedByCreatedDateDesc() throws Exception {
        mockMvc.perform(get("/api/movie/review")
                        .param("movieId", "20070001")
                        .param("sort", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ReviewSuccessCode.REVIEW_LIST_FETCHED.getCode()))
                .andExpect(jsonPath("$.data.reviews[0].content").value("Amazing movie!"))
                .andExpect(jsonPath("$.data.totalRating", closeTo(4.0, 0.5)));
    }

    @Test
    @DisplayName("리뷰 목록 조회 - 좋아요순 정렬")
    void getReviewsSortedByLikeSuccess() throws Exception {
        mockMvc.perform(get("/api/movie/review")
                        .param("movieId", "20070001")
                        .param("sort", "like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reviews[0].likeCount").value(8))
                .andExpect(jsonPath("$.data.reviews[1].likeCount").value(6));
    }
}
