package com.NBE4_5_SukChanHoSu.BE.domain.movie.dto.response;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.entity.MovieGenre;
import lombok.Data;

import java.util.List;

@Data
public class MovieResponse {
    private String movieNm; // 영화명
    private String openDt; // 개봉일
    private String showTm;  // 상영 시간
    private String director;
//    private Long audiAcc; // 관객수
    private String actors; // 배우
    private List<MovieGenre> genres; // 장르
    private String watchGradeNm; // 연령제한
    private String posterUrl; // 포스터 URL
    private String overview;    // 줄거리

    public MovieResponse(String movieNm, String openDt,String showTm, String director, List<MovieGenre> genres, String actors, String watchGradeNm, String posterUrl, String overview) {
        this.movieNm = movieNm;
        this.openDt = openDt;
        this.showTm = showTm;
        this.director = director;
//        this.audiAcc = audiAcc;
        this.genres = genres;
        this.actors = actors;
        this.watchGradeNm = watchGradeNm;
        this.posterUrl = posterUrl;
        this.overview = overview;
    }
}