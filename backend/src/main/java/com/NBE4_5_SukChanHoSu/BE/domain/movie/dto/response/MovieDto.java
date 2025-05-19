package com.NBE4_5_SukChanHoSu.BE.domain.movie.dto.response;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private Long movieId;
    private String title;
    private String posterUrl;

    public static MovieDto from(Movie movie) {
        return new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getPosterImage()
        );
    }
}
