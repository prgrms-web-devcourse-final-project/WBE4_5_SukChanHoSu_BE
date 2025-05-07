package com.NBE4_5_SukChanHoSu.BE.domain.movie.service;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.dto.MovieResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.dto.WeeklyBoxOfficeResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    @Value("${movie.api.key}")
    private String kobisApiKey; // KOBIS API 키

    @Value("${movie.api.key2}")
    private String tmdbApiKey; // TMDB API 키

    @Value("${movie.api.rank-url}")
    private String rankUrl; // KOBIS 주간 박스오피스 API URL

    @Value("${movie.api.detail-url}")
    private String detailUrl; // KOBIS 영화 상세 정보 API URL

    @Value("${movie.api.tmdb-search-url}")
    private String tmdbSearchUrl; // TMDB 영화 검색 API URL

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public List<MovieResponse> searchWeeklyBoxOffice(String targetDt, String weekGb, String itemPerPage) {
        // KOBIS 주간 박스오피스 조회 API 요청 URL 생성
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(rankUrl)
                .queryParam("key", kobisApiKey)
                .queryParam("targetDt", targetDt)
                .queryParam("weekGb", weekGb)
                .queryParam("itemPerPage", itemPerPage);

        String requestUrl = builder.toUriString();

        String jsonResponse;
        try {
            jsonResponse = restTemplate.getForObject(requestUrl, String.class);
            if (jsonResponse == null || jsonResponse.isEmpty()) {
                throw new RuntimeException("KOBIS API returned empty response");
            }
            System.out.println("KOBIS API Response: " + jsonResponse);
        } catch (Exception e) {
            System.err.println("Error during KOBIS API request: " + e.getMessage());
            return Collections.emptyList(); // 빈 리스트 반환
        }

        // JSON 파싱 및 데이터 처리
        try {
            WeeklyBoxOfficeResult result = objectMapper.readValue(jsonResponse, WeeklyBoxOfficeResult.class);
            if (result == null) {
                throw new RuntimeException("KOBIS API response parsing failed: result is null");
            }

            List<WeeklyBoxOfficeResult.WeeklyBoxOffice> boxOfficeList = result.getBoxOfficeResult().getWeeklyBoxOfficeList();
            if (boxOfficeList == null || boxOfficeList.isEmpty()) {
                return Collections.emptyList(); // 빈 리스트 반환
            }

            // 영화 상세 정보 조회
            return boxOfficeList.stream()
                    .map(movie -> {
                        Map<String, Object> detail = getMovieDetail(movie.getMovieCd()); // KOBIS 상세 정보 조회
                        String posterUrl = getMoviePoster(movie.getMovieNm()); // TMDB에서 포스터 URL 조회
                        return new MovieResponse(
                                movie.getRank(), // 순위
                                movie.getMovieNm(), // 영화명
                                movie.getOpenDt(), // 개봉일
                                movie.getAudiCnt(), // 관객수
                                (String) detail.get("genres"), // 장르
                                (String) detail.get("actors"), // 배우
                                (String) detail.get("watchGradeNm"), // 연령제한
                                posterUrl // 포스터 URL
                        );
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error parsing KOBIS JSON response: " + e.getMessage());
            return Collections.emptyList(); // 빈 리스트 반환
        }
    }

    private Map<String, Object> getMovieDetail(String movieCd) {
        // KOBIS 영화 상세 정보 조회 API 요청 URL 생성
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(detailUrl)
                .queryParam("key", kobisApiKey)
                .queryParam("movieCd", movieCd);

        String requestUrl = builder.toUriString();
        System.out.println("KOBIS Movie Info API Request URL: " + requestUrl);

        // API 요청 및 응답 받기
        String jsonResponse;
        try {
            jsonResponse = restTemplate.getForObject(requestUrl, String.class);
            if (jsonResponse == null || jsonResponse.isEmpty()) {
                throw new RuntimeException("KOBIS Movie Info API returned empty response");
            }
            System.out.println("KOBIS Movie Info API Response: " + jsonResponse);
        } catch (Exception e) {
            System.err.println("Error during KOBIS Movie Info API request: " + e.getMessage());
            return Map.of(
                    "genres", "장르 정보 없음",
                    "actors", "배우 정보 없음",
                    "watchGradeNm", "연령제한 정보 없음"
            );
        }

        // JSON 파싱 및 정보 추출
        try {
            Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, new TypeReference<Map<String, Object>>() {});
            Map<String, Object> movieInfoResult = (Map<String, Object>) responseMap.get("movieInfoResult");
            if (movieInfoResult == null) {
                throw new RuntimeException("movieInfoResult is null");
            }

            Map<String, Object> movieInfo = (Map<String, Object>) movieInfoResult.get("movieInfo");
            if (movieInfo == null) {
                throw new RuntimeException("movieInfo is null");
            }

            // 장르 정보 추출
            List<Map<String, String>> genres = (List<Map<String, String>>) movieInfo.get("genres");
            String genreString = genres == null || genres.isEmpty()
                    ? "장르 정보 없음"
                    : genres.stream()
                    .map(genre -> genre.get("genreNm"))
                    .collect(Collectors.joining(", "));

            // 배우 정보 추출
            List<Map<String, String>> actors = (List<Map<String, String>>) movieInfo.get("actors");
            String actorString = actors == null || actors.isEmpty()
                    ? "배우 정보 없음"
                    : actors.stream()
                    .map(actor -> actor.get("peopleNm"))
                    .collect(Collectors.joining(", "));

            // 연령제한 정보 추출
            List<Map<String, String>> audits = (List<Map<String, String>>) movieInfo.get("audits");
            String watchGradeNm = audits == null || audits.isEmpty()
                    ? "연령제한 정보 없음"
                    : audits.get(0).get("watchGradeNm");

            // 결과 Map 반환
            return Map.of(
                    "genres", genreString,
                    "actors", actorString,
                    "watchGradeNm", watchGradeNm
            );
        } catch (Exception e) {
            return Map.of(
                    "genres", "장르 정보 없음",
                    "actors", "배우 정보 없음",
                    "watchGradeNm", "연령제한 정보 없음"
            );
        }
    }

    private String getMoviePoster(String movieNm) {
        // TMDB 영화 검색 API 요청 URL 생성
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(tmdbSearchUrl)
                .queryParam("api_key", tmdbApiKey)
                .queryParam("query", movieNm);

        String requestUrl = builder.toUriString();
        System.out.println("TMDB API Request URL: " + requestUrl);

        // API 요청 및 응답 받기
        String jsonResponse;
        try {
            jsonResponse = restTemplate.getForObject(requestUrl, String.class);
            if (jsonResponse == null || jsonResponse.isEmpty()) {
                throw new RuntimeException("TMDB API returned empty response");
            }
            System.out.println("TMDB API Response: " + jsonResponse);
        } catch (Exception e) {
            System.err.println("Error during TMDB API request: " + e.getMessage());
            return "포스터 정보 없음";
        }

        // JSON 파싱 및 포스터 URL 추출
        try {
            Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, new TypeReference<Map<String, Object>>() {});
            List<Map<String, Object>> results = (List<Map<String, Object>>) responseMap.get("results");
            if (results == null || results.isEmpty()) {
                return "포스터 정보 없음";
            }

            // 첫 번째 결과에서 포스터 URL 추출
            String posterPath = (String) results.get(0).get("poster_path");
            if (posterPath == null || posterPath.isEmpty()) {
                return "포스터 정보 없음";
            }

            // 포스터 URL 생성
            return "https://image.tmdb.org/t/p/w500" + posterPath;
        } catch (Exception e) {
            System.err.println("Error parsing TMDB JSON response: " + e.getMessage());
            return "포스터 정보 없음";
        }
    }
}