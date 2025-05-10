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
    @Column(name = "movie_id", nullable = false, length = 20)
    private Long movieId; // = movieCd

    @Column(name = "genre")
    private Genre genre; // = genres

    @Column(name = "title", nullable = false)
    private String title; // = movieNm

    @Column(name = "release_year")
    private String releaseYear; // = openDt 데이터 파싱 필요

    @Column(name = "poster_image", length = 1000)
    private String posterImage; // = posterUrl

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // = overview

    @Column(name = "rating")
    private String rating; // = 미지정

    @Column(name = "director", length = 100)
    private String director; // = director
}