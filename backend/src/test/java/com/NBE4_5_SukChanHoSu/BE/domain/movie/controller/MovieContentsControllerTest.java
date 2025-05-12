package com.NBE4_5_SukChanHoSu.BE.domain.movie.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.dto.request.MovieRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.entity.Movie;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.service.MovieContentsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieContentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieContentsService movieContentsService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // 필요한 테스트 데이터 셋업
    }

    @Test
    @DisplayName("영화 생성")
    void createMovie() throws Exception {
        MovieRequest request = new MovieRequest();
        request.setMovieId(1L);
        request.setTitle("Inception");

        ResultActions result = mockMvc.perform(post("/api/movie")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)))
                .andDo(print());

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Inception"));
    }

    @Test
    @DisplayName("영화 전체 조회")
    void getAllMovies() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/movie/list"))
                .andDo(print());

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @DisplayName("영화 페이징 조회")
    void getAllMoviesPaged() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/movie/paged?page=0&size=10"))
                .andDo(print());

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @DisplayName("영화 단건 조회")
    void getMovieById() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/movie/1"))
                .andDo(print());

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @DisplayName("영화 제목으로 검색")
    void searchByTitle() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/movie/search/title?title=Inception"))
                .andDo(print());

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @DisplayName("영화 장르로 검색")
    void searchByGenre() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/movie/search/genre?genre=DRAMA"))
                .andDo(print());

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @DisplayName("영화 삭제")
    void deleteMovie() throws Exception {
        ResultActions result = mockMvc.perform(delete("/api/movie/1"))
                .andDo(print());

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("204"));
    }

    @Test
    @DisplayName("영화 수정")
    void updateMovie() throws Exception {
        Movie movie = Movie.builder()
                .movieId(1L)
                .title("Updated Title")
                .build();

        ResultActions result = mockMvc.perform(put("/api/movie/1")
                        .content(objectMapper.writeValueAsString(movie))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)))
                .andDo(print());

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Updated Title"));
    }
}

