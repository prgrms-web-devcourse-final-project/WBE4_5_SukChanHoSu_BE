package com.NBE4_5_SukChanHoSu.BE.domain.movie.dto.request;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieRequest {

    @NotNull
    private Long movieId; // = movieCd

    @NotNull
    private Genre genres; // = genres

    @NotBlank
    private String title; // = movieNm

    private String releaseDate; // = openDt

    private String posterImage; // = posterUrl

    private String description; // = overview

    private String rating; // = 미지정

    private String director; // = director
}