package com.NBE4_5_SukChanHoSu.BE.domain.movie.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.dto.MovieResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.service.MovieService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieApiService) {
        this.movieService = movieApiService;
    }

    @GetMapping("/movies")
    public List<MovieResponse> searchMovies(
            @RequestParam String targetDt,
            @RequestParam(defaultValue = "0") String weekGb,
            @RequestParam(defaultValue = "10") String itemPerPage) {
        return movieService.searchWeeklyBoxOffice(targetDt, weekGb, itemPerPage);
    }
}