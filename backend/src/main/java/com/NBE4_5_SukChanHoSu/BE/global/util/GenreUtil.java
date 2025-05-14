package com.NBE4_5_SukChanHoSu.BE.global.util;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;

import java.util.List;
import java.util.stream.Collectors;

public class GenreUtil {

    /**
     * 장르 리스트를 쉼표로 구분된 문자열로 변환합니다.
     * 예: [Genre.ACTION, Genre.DRAMA] → "ACTION, DRAMA"
     */
    public static String convertGenreListToRaw(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return null;
        }
        return genres.stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}