package com.NBE4_5_SukChanHoSu.BE.domain.movie.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.dto.MovieRankingResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.dto.MovieResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.service.MovieService;
import com.NBE4_5_SukChanHoSu.BE.global.util.DateUtils;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/movie")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieApiService) {
        this.movieService = movieApiService;
    }

    @GetMapping("/weekly")
    public List<MovieRankingResponse> searchWeeklyBoxOffice(
            @RequestParam(required = false) String targetDt,    // 필수 파라미터 x
            @RequestParam(defaultValue = "0") String weekGb,
            @RequestParam(defaultValue = "10") String itemPerPage) {

        if(targetDt == null || targetDt.isEmpty()) {
            targetDt = DateUtils.getOneWeekAgoDate();   // 1주일전
        }

        return movieService.searchWeeklyBoxOffice(targetDt, weekGb, itemPerPage);
    }

    // 영화 상세 정보 조회
    @GetMapping("/detail")
    public MovieResponse getMovieDetail(@RequestParam String movieCd) {
        return movieService.getMovieDetail(movieCd);
    }
}