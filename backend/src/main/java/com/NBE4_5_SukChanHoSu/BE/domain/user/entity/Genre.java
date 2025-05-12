package com.NBE4_5_SukChanHoSu.BE.domain.user.entity;

import java.util.Arrays;
import java.util.List;

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

    public static Genre fromLabel(String label) {
        return Arrays.stream(Genre.values())
                .filter(g -> g.label.equalsIgnoreCase(label.trim()))
                .findFirst()
                .orElse(UNKNOWN);
    }

    // 복수개의 장르 문자열 -> List 장르로 파싱
    public static List<Genre> parseGenres(String input) {
        if (input == null || input.isBlank()) {
            return List.of(Genre.UNKNOWN);
        }

        return Arrays.stream(input.split(","))
                .map(String::trim)
                .map(Genre::fromLabel)  // ← 대소문자/공백 처리 포함된 from 메서드 호출
                .distinct()
                .toList();
    }
}
