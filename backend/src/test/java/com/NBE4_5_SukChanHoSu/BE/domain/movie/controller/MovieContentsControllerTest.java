package com.NBE4_5_SukChanHoSu.BE.domain.movie.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.dto.request.MovieRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.entity.Movie;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.service.MovieContentsService;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserLoginRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieContentsControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private MovieContentsService movieContentsService;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserService userService;

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
    @DisplayName("영화 생성")
    void createMovie() throws Exception {
        MovieRequest request = new MovieRequest();
        request.setMovieId(1L);
        request.setTitle("Inception");

        mockMvc.perform(post("/api/movie")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Inception"));
    }

    @Test
    @DisplayName("영화 전체 조회")
    void getAllMovies() throws Exception {
        mockMvc.perform(get("/api/movie/list")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @DisplayName("영화 페이징 조회")
    void getAllMoviesPaged() throws Exception {
        mockMvc.perform(get("/api/movie/paged?page=0&size=10")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @DisplayName("영화 단건 조회")
    void getMovieById() throws Exception {
        mockMvc.perform(get("/api/movie/1")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @DisplayName("영화 제목으로 검색")
    void searchByTitle() throws Exception {
        mockMvc.perform(get("/api/movie/search/title?title=Inception")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @DisplayName("영화 장르로 검색")
    void searchByGenre() throws Exception {
        mockMvc.perform(get("/api/movie/search/genre?genre=DRAMA")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @DisplayName("영화 삭제")
    void deleteMovie() throws Exception {
        mockMvc.perform(delete("/api/movie/20030410")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("204"));
    }

    @Test
    @DisplayName("영화 수정")
    void updateMovie() throws Exception {
        Movie movie = Movie.builder()
                .movieId(20030410L)
                .title("Updated Title")
                .build();

        mockMvc.perform(put("/api/movie/1")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(movie))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Updated Title"));
    }
}

