package com.NBE4_5_SukChanHoSu.BE.domain.movie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class WeeklyBoxOfficeResult {
    @JsonProperty("boxOfficeResult")
    private BoxOfficeResult boxOfficeResult;

    @Data
    public static class BoxOfficeResult {
        @JsonProperty("weeklyBoxOfficeList")
        private List<WeeklyBoxOffice> weeklyBoxOfficeList;
    }

    @Data
    public static class WeeklyBoxOffice {
        @JsonProperty("rank")
        private int rank; // 순위
        @JsonProperty("movieNm")
        private String movieNm; // 영화명
//        @JsonProperty("openDt")
//        private String openDt; // 개봉일
        @JsonProperty("audiAcc")
        private long audiAcc; // 관객수
        @JsonProperty("movieCd")
        private String movieCd; // 영화 코드
    }
}
