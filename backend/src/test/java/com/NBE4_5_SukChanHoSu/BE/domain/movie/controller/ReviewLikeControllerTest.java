package com.NBE4_5_SukChanHoSu.BE.domain.movie.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.entity.Movie;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.repository.MovieRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.entity.Review;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.repository.ReviewRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserLoginRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.responseCode.ReviewErrorCode;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.responseCode.ReviewSuccessCode;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
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
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserService userService;

    private String accessToken;

    private Long reviewId;

    @BeforeEach
    void setUp() {
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setEmail("initUser1@example.com");
        loginRequest.setPassword("testPassword123!");
        accessToken = userService.login(loginRequest).getAccessToken();

        // 테스트용 리뷰 삽입
        Movie movie = movieRepository.findById(20070001L).get();
        User user = userRepository.findByEmail("initUser1@example.com");
        Review review = Review.builder()
                .movie(movie)
                .user(user)
                .likeCount(0)
                .content("This is a test review")
                .rating(4.9)
                .build();
        reviewId = reviewRepository.save(review).getId();
    }

    @Test
    @DisplayName("리뷰 좋아요 - 성공적으로 좋아요 추가")
    void addLikeSuccessFirstTime() throws Exception {
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
        // 1차 요청: 좋아요 누르기
        mockMvc.perform(post("/api/review/like/" + reviewId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.liked", is(true)));

        // 2차 요청: 다시 눌러서 좋아요 취소
        mockMvc.perform(post("/api/review/like/" + reviewId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ReviewSuccessCode.REVIEW_LIKE_CANCEL.getCode()))
                .andExpect(jsonPath("$.data.liked", is(false)));
    }

    @Test
    @DisplayName("리뷰 좋아요 - 리뷰가 존재하지 않을 때 예외 발생")
    void addLikeReviewNotFound() throws Exception {
        long notFoundReviewId = 1231245L;
        mockMvc.perform(post("/api/review/like/" + notFoundReviewId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ReviewErrorCode.REVIEW_NOT_FOUND.getCode()));
    }
}
