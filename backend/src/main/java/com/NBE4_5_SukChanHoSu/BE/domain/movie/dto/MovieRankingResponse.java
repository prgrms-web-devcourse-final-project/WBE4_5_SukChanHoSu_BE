package com.NBE4_5_SukChanHoSu.BE.domain.movie.dto;

import lombok.Data;

@Data
public class MovieRankingResponse {
    private int rank; // 순위
    private String movieNm; // 영화명
    private String posterUrl; // 포스터 URL
    private String movieCd;
    private long audiAcc;

    public MovieRankingResponse(int rank, String movieNm, String posterUrl, long audiAcc, String movieCd) {
        this.rank = rank;
        this.movieNm = movieNm;
        this.posterUrl = posterUrl;
        this.movieCd = movieCd;
        this.audiAcc = audiAcc;
    }
}