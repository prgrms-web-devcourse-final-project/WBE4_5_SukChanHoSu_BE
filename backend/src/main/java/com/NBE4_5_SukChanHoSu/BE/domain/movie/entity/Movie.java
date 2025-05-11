package com.NBE4_5_SukChanHoSu.BE.domain.movie.entity;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @Column(name = "movieCd", nullable = false, length = 20)
    private Long movieId; // = movieCd

    @Column(name = "genres")
    private Genre genres; // = genres

    @Column(name = "movieNm", nullable = false)
    private String title; // = movieNm

    @Column(name = "openDt")
    private String releaseDate; // = openDt 데이터 파싱 필요

    @Column(name = "posterUrl", length = 1000)
    private String posterImage; // = posterUrl

    @Column(name = "overview", columnDefinition = "TEXT")
    private String description; // = overview

    @Column(name = "rating")
    private String rating; // = 미지정

    @Column(name = "director", length = 100)
    private String director; // = director
}