package com.NBE4_5_SukChanHoSu.BE.domain.movie;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UpdateMovieDto {

    private String title;

    private Long tId;

    private int releaseYear;

    private String posterImage;

    private String description;

    private double rating;

    private String director;

    private Set<Genre> genres; // 장르 객체들 (혹은 genreId 리스트로 바꿔도 됨)

    public void applyTo(Movie movie) {
        movie.setTitle(this.title);
        movie.setTId(this.tId);
        movie.setReleaseYear(this.releaseYear);
        movie.setPosterImage(this.posterImage);
        movie.setDescription(this.description);
        movie.setRating(this.rating);
        movie.setDirector(this.director);
        movie.setGenres(this.genres);
    }
}
