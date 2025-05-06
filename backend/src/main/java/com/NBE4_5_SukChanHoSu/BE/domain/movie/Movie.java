package com.NBE4_5_SukChanHoSu.BE.domain.movie;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@NamedEntityGraph(name="Movies.withGenres", attributeNodes = {
        @NamedAttributeNode("genres")
})
@Entity @Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    private Long id;

    private String title;

    private Long tId;

    @ManyToMany
    private Set<Genre> genres;

}
