package com.NBE4_5_SukChanHoSu.BE.domain.movie.dto;

import lombok.Data;

@Data
public class MovieRankingResponse {
    private int rank; // 순위
    private String movieNm; // 영화명
    private String posterUrl; // 포스터 URL
    private String movieCd;
    private String audiAcc;

    public MovieRankingResponse(int rank, String movieNm, String posterUrl, long audiAcc, String movieCd) {
        this.rank = rank;
        this.movieNm = movieNm;
        this.posterUrl = posterUrl;
        this.movieCd = movieCd;
        this.audiAcc = formatAudiAcc(audiAcc);
    }

    // 관객수를 만 단위로 변환 (천 단위는 버림)
    private String formatAudiAcc(long audiAcc) {
        long audiAccInTenThousand = audiAcc / 10000; // 만 단위로 변환
        return audiAccInTenThousand + "만";
    }
}