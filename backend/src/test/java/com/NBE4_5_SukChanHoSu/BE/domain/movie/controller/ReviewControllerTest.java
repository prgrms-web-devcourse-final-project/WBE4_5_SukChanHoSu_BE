package com.NBE4_5_SukChanHoSu.BE.domain.movie.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.controller.ReviewController;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.request.ReviewCreateDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.request.ReviewUpdateDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response.AllReviewDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response.ReviewResponseDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.entity.Review;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

        ReviewResponseDto reviewResponse = new ReviewResponseDto();
        reviewResponse.setId(20070001L);
        reviewResponse.setTitle("Inception");
        reviewResponse.setContent("Amazing movie!");
        reviewResponse.setRating(5.0);

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
        Long reviewId = 1L;

        ReviewResponseDto responseDto = reviewService.getOneReview(1L);

        when(reviewService.getOneReview(reviewId)).thenReturn(responseDto);

        mockMvc.perform(get("/api/movie/review/{id}", reviewId)
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ReviewSuccessCode.REVIEW_FETCHED.getCode()))
                .andExpect(jsonPath("$.message").value(ReviewSuccessCode.REVIEW_FETCHED.getMessage()))
                .andExpect(jsonPath("$.data.title").value("Inception"));
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReviewSuccess() throws Exception {
        Long reviewId = 1L;
        ReviewUpdateDto updateDto = new ReviewUpdateDto();
        updateDto.setContent("is good");
        updateDto.setRating(4.0);

        doNothing().when(reviewService).updateReview(eq(reviewId), any());

        mockMvc.perform(put("/api/movie/review/{id}", reviewId)
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
        doNothing().when(reviewService).deleteReview(reviewId);

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
        ReviewResponseDto review1 = reviewService.getOneReview(1L);
        ReviewResponseDto review2 = reviewService.getOneReview(2L);
        double rating = review1.getRating() + review2.getRating();
        AllReviewDto allReviewDto = new AllReviewDto(List.of(review1, review2), 2L, rating);

        when(reviewService.getAllReviewsByMovieId(eq(1L), eq(""))).thenReturn(allReviewDto);

        mockMvc.perform(get("/api/movie/review")
                        .param("movieId", "20070001L")
                        .param("sort", " "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ReviewSuccessCode.REVIEW_LIST_FETCHED.getCode()))
                .andExpect(jsonPath("$.data.reviews[0].content").value("꿈속의 꿈으로 들어가는 액션 블록버스터"))
                .andExpect(jsonPath("$.data.totalRating").value(4.3)); // 소수점 반올림 확인
    }

    @Test
    @DisplayName("리뷰 목록 조회 - 좋아요순 정렬")
    void getReviewsSortedByLikeSuccess() throws Exception {
        ReviewResponseDto review1 = new ReviewResponseDto();
        review1.setId(1L);
        review1.setTitle("인셉션");
        review1.setContent("정말 좋았어요");
        review1.setRating(4.5);
        review1.setLikeCount(10);

        ReviewResponseDto review2 = new ReviewResponseDto();
        review2.setId(2L);
        review2.setTitle("인셉션");
        review2.setContent("최고의 영화입니다");
        review2.setRating(4.0);
        review2.setLikeCount(8);

        AllReviewDto allReviewDto = new AllReviewDto(List.of(review1, review2), 2L, 4.25);

        when(reviewService.getAllReviewsByMovieId(eq(1L), eq("like"))).thenReturn(allReviewDto);

        mockMvc.perform(get("/api/movie/review")
                        .param("movieId", "1")
                        .param("sort", "like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reviews[0].likeCount").value(10))
                .andExpect(jsonPath("$.data.reviews[1].likeCount").value(8));
    }
}
