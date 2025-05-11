package com.NBE4_5_SukChanHoSu.BE.domain.movie.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.dto.MovieRankingResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.dto.MovieResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.service.MovieApiService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import com.NBE4_5_SukChanHoSu.BE.global.util.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movie")
public class MovieApiController {

    private final MovieApiService movieApiService;

    public MovieApiController(MovieApiService movieApiService) {
        this.movieApiService = movieApiService;
    }

    @Operation(summary = "박스오피스 조회", description = "1주일전 박스 오피스 탑텐 조회")
    @GetMapping("/weekly")
    public RsData<List<MovieRankingResponse>> searchWeeklyBoxOffice(
            @RequestParam(required = false) String targetDt,    // 필수 파라미터 x
            @RequestParam(defaultValue = "0") String weekGb,
            @RequestParam(defaultValue = "10") String itemPerPage) {

        if(targetDt == null || targetDt.isEmpty()) {
            targetDt = DateUtils.getOneWeekAgoDate();   // 1주일전
        }
        List<MovieRankingResponse> responses = movieApiService.searchWeeklyBoxOffice(targetDt, weekGb, itemPerPage);
        return new RsData<>("200","박스 오피스 Top 10(일자: "+targetDt+")", responses);
    }

    // 영화 상세 정보 조회
    @Operation(summary = "영화 상세 정보 조회", description = "영화 넘버를 이용하여 상세 정보 조회")
    @GetMapping("/detail")
    public RsData<MovieResponse> getMovieDetail(@RequestParam String movieCd) {
        MovieResponse response = movieApiService.getMovieDetail(movieCd);
        return new RsData<>("200","영화 상세 정보", response);

    }
}