package com.NBE4_5_SukChanHoSu.BE.domain.movie.dto.request;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
import com.NBE4_5_SukChanHoSu.BE.global.util.GenreStringToListConverter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MovieRequest {

    @NotNull
    private Long movieId;

    @NotNull
    @JsonDeserialize(using = GenreStringToListConverter.class)
    private List<Genre> genres;  // ← 복수 장르 대응

    @NotBlank
    private String title;

    private String releaseDate;
    private String posterImage;
    private String description;
    private String rating;
    private String director;
}