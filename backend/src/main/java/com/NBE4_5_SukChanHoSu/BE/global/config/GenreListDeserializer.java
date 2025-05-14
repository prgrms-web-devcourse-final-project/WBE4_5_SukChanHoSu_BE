package com.NBE4_5_SukChanHoSu.BE.global.config;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenreListDeserializer extends JsonDeserializer<List<Genre>> {
    @Override
    public List<Genre> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<String> genreStrings = p.readValueAs(new TypeReference<List<String>>() {});
        List<Genre> genres = new ArrayList<>();
        for (String s : genreStrings) {
            try {
                genres.add(Genre.valueOf(s.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("직렬화 실패");
            }
        }
        return genres;
    }
}
