package com.NBE4_5_SukChanHoSu.BE.global.util;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenreDeserializer extends JsonDeserializer<List<Genre>> {

    /**
     * 배열 형식과 문자열 형식 모두를 지원하는 Genre 역직렬화기
     *
     * 지원 JSON 입력:
     * - "ACTION, DRAMA"
     * - ["ACTION", "DRAMA"]
     */
    @Override
    public List<Genre> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        if (node.isArray()) {
            List<Genre> result = new ArrayList<>();
            for (JsonNode element : node) {
                result.add(Genre.valueOf(element.asText().toUpperCase()));
            }
            return result;
        } else {
            String input = node.asText();
            return Genre.parseGenres(input);
        }
    }
}