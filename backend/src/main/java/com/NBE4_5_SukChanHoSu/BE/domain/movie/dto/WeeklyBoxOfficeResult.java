package com.NBE4_5_SukChanHoSu.BE.domain.movie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WeeklyBoxOfficeResult {
    @JsonProperty("boxOfficeResult")
    private BoxOfficeResult boxOfficeResult;

    @Getter
    @Setter
    public static class BoxOfficeResult {
        @JsonProperty("weeklyBoxOfficeList")
        private List<WeeklyBoxOffice> weeklyBoxOfficeList;
    }

    @Getter
    @Setter
    public static class WeeklyBoxOffice {
        @JsonProperty("rank")
        private String rank; // 순위
        @JsonProperty("movieNm")
        private String movieNm; // 영화명
        @JsonProperty("openDt")
        private String openDt; // 개봉일
        @JsonProperty("audiCnt")
        private String audiCnt; // 관객수
        @JsonProperty("movieCd")
        private String movieCd; // 영화 코드
    }
}
