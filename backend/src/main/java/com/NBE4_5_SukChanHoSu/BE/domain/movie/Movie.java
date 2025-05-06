package com.NBE4_5_SukChanHoSu.BE.domain.movie;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@NamedEntityGraph(
        name = "Movie.withGenres",
        attributeNodes = @NamedAttributeNode("genres")
)
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    private Long id; // 외부 시스템에서 ID 수동 지정 (e.g. TMDB ID)

    private String title;

    private Long tId; // TMDB 고유 ID (중복 방지용이면 unique 추천)

    private int releaseYear;

    private String posterImage;  // ✅ 카멜케이스로 통일 (DB 컬럼 명은 자동 매핑됨)

    private String description;

    private double rating;

    private String director;     // ✅ 오타 수정: 'diretor' → 'director'

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;
}