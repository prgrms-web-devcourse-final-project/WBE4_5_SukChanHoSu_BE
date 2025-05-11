package com.NBE4_5_SukChanHoSu.BE.domain.user.entity;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.entity.Movie;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum Genre {
    // Java에선 Action, mysql에선 Action으로 취급됨
    ACTION("Action"),
    ADVENTURE("Adventure"),
    ANIMATION("Animation"),
    COMEDY("Comedy"),
    CRIME("Crime"),
    DOCUMENTARY("Documentary"),
    DRAMA("Drama"),
    FAMILY("Family"),
    FANTASY("Fantasy"),
    HISTORY("History"),
    HORROR("Horror"),
    MUSIC("Music"),
    MYSTERY("Mystery"),
    ROMANCE("Romance"),
    SCIENCE_FICTION("Science Fiction"),
    TV_MOVIE("TV Movie"),
    THRILLER("Thriller"),
    WAR("War"),
    WESTERN("Western"),
    UNKNOWN("장르 정보 없음");

    private final String label;

    /* 자바에서 대소문자 공존(Action) 문자열을 취급위해
    Genre genre = Genre.ACTION;             // "ACTION"
    System.out.println(genre);        // "ACTION"
    System.out.println(genre.name());       // "ACTION"
    System.out.println(genre.getLabel());   // "Action"
    */
    Genre(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static Genre from(String input) {
        if (input == null || input.isBlank()) return UNKNOWN;

        return Stream.of(values())
                .filter(g -> g.label.equalsIgnoreCase(input.trim()))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
