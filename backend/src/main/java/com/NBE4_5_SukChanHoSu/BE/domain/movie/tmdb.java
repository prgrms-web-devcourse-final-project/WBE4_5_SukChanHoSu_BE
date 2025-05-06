package com.NBE4_5_SukChanHoSu.BE.domain.movie;

import org.springframework.web.reactive.function.client.WebClient;

public class tmdb {
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.themoviedb.org/3/search/movie?include_adult=false&language=en-US&page=1")
            .defaultHeader("accept", "application/json")
            .defaultHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4NWFmNmZmOGYwZjljYjE3NjNhOGIzMjBiNjc0N2ViMyIsIm5iZiI6MTc0NjQ1OTUwMi40Miwic3ViIjoiNjgxOGRiNmVjNjlhZWNlZGQ5OTEzOTQ4Iiwic2NvcGVzIjpbImFwaV9yZWFkIl0sInZlcnNpb24iOjF9.Qe66C6TYM-TFmem3qAgAL2VtUuUL5PbpMxKctyuMdaw")
            .build();

    public String callAuthApi() {
        return webClient.get()
                .uri("/authentication")
                .retrieve()
                .bodyToMono(String.class)
                .block(); // ⚠️ 비동기 => 동기 호출로 대기 (테스트용)
    }
}
