package com.NBE4_5_SukChanHoSu.BE.domain.movie.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieResponse {
    private String rank; // 순위
    private String movieNm; // 영화명
    private String openDt; // 개봉일
    private String audiCnt; // 관객수
    private String genres; // 장르
    private String actors; // 배우
    private String watchGradeNm; // 연령제한
    private String posterUrl; // 포스터 URL

    public MovieResponse(String rank, String movieNm, String openDt, String audiCnt, String genres, String actors, String watchGradeNm, String posterUrl) {
        this.rank = rank;
        this.movieNm = movieNm;
        this.openDt = openDt;
        this.audiCnt = audiCnt;
        this.genres = genres;
        this.actors = actors;
        this.watchGradeNm = watchGradeNm;
        this.posterUrl = posterUrl;
    }
}