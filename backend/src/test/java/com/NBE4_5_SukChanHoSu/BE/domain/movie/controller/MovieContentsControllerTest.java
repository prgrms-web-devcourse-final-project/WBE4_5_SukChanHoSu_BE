package com.NBE4_5_SukChanHoSu.BE.domain.movie.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.dto.request.MovieRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.entity.Movie;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.service.MovieContentsService;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserLoginRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
        // ✅ 먼저 영화 저장
        List<Genre> genreList = List.of(Genre.ACTION, Genre.SCIENCE_FICTION);

        MovieRequest movie = new MovieRequest();
        movie.setMovieId(20070441L);
        movie.setTitle("Interstellar");
        movie.setGenres(genreList);
        movie.setReleaseDate("20141107");
        movie.setPosterImage("https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg");
        movie.setDescription("우주의 끝에서 미래를 찾다");
        movie.setRating("PG-13");
        movie.setDirector("Christopher Nolan");
        mockMvc.perform(post("/api/movie")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/movie/20070441")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // ✅ 그 다음 삭제
        mockMvc.perform(delete("/api/movie/20070441")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("204"));
    }

    @Test
    @DisplayName("영화 수정")
    void updateMovie() throws Exception {
        List<Genre> genreList = List.of(Genre.ACTION, Genre.SCIENCE_FICTION);

        MovieRequest movie = new MovieRequest();
        movie.setMovieId(20070555L);
        movie.setTitle("Interstellar");
        movie.setGenres(genreList);
        movie.setReleaseDate("20141107");
        movie.setPosterImage("https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg");
        movie.setDescription("우주의 끝에서 미래를 찾다");
        movie.setRating("PG-13");
        movie.setDirector("Christopher Nolan");
        mockMvc.perform(post("/api/movie")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/movie/20070555")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(movie))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Interstellar"));
    }
}