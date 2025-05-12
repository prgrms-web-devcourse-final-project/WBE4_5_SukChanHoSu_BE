package com.NBE4_5_SukChanHoSu.BE.domain.movie.entity;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    private String genresRaw; // = genres

    @Transient
    private List<Genre> genres; // 로직에서 사용

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

    @PostLoad
    private void onLoad() {
        if (genresRaw != null && !genresRaw.isBlank()) {
            this.genres = Arrays.stream(genresRaw.split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .map(Genre::valueOf)
                    .collect(Collectors.toList());
        } else {
            this.genres = new ArrayList<>();
        }
    }

    @PrePersist
    @PreUpdate
    private void onSave() {
        if (genres != null && !genres.isEmpty()) {
            this.genresRaw = genres.stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
        } else {
            this.genresRaw = null;
        }
    }
}