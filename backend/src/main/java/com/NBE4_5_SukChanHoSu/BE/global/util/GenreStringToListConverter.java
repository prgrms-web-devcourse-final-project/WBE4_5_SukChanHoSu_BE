package com.NBE4_5_SukChanHoSu.BE.global.util;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.List;

public class GenreStringToListConverter extends JsonDeserializer<List<Genre>> {

    @Override
    public List<Genre> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String input = p.getText();
        return Genre.parseGenres(input);  // "Action, Comedy" → [Genre.ACTION, Genre.COMEDY]
    }
}