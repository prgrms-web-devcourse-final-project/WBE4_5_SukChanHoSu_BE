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
        System.out.println("🎯 GenreDeserializer activated");

        if (node.isArray()) {
            List<Genre> result = new ArrayList<>();
            for (JsonNode element : node) {
                String name = element.asText();
                try {
                    Genre genre = Genre.valueOf(name);  // ← enum name 그대로 매핑
                    System.out.println("🎯 GenreDeserializer element: " + name + " -> " + genre);
                    result.add(genre);
                } catch (IllegalArgumentException e) {
                    System.out.println("⚠️ Unknown genre name: " + name + ", defaulting to UNKNOWN");
                    result.add(Genre.UNKNOWN);
                }
            }
            return result;
        } else {
            String input = node.asText();
            List<Genre> parsed = Genre.parseGenres(input);
            System.out.println("🎯 GenreDeserializer input: " + input + " -> " + parsed);
            return parsed;
        }
    }
}