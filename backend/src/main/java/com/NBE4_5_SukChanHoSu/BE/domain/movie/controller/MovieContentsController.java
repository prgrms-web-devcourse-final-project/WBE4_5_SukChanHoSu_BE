package com.NBE4_5_SukChanHoSu.BE.domain.movie.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.dto.request.MovieRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.entity.Movie;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.service.MovieContentsService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import com.NBE4_5_SukChanHoSu.BE.global.exception.ServiceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "영화", description = "영화 정보 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movie")
public class MovieContentsController {

    private final MovieContentsService movieContentsService;

    @Operation(summary = "영화 생성", description = "영화 데이터를 생성합니다.")
    @PostMapping
    public RsData<Movie> createMovie(@RequestBody MovieRequest request) {
        Movie saved = movieContentsService.save(request.toEntity());
        return new RsData<>("200", "영화 생성 완료", saved);
    }

    @Operation(summary = "영화 전체 조회", description = "저장된 모든 영화를 조회합니다.")
    @GetMapping("/list")
    public RsData<List<Movie>> getAllMovies() {
        List<Movie> movies = movieContentsService.findAll();
        return new RsData<>("200", "영화 목록 조회", movies);
    }

    @Operation(summary = "영화 전체 조회 (페이징)", description = "저장된 모든 영화를 페이지 단위로 조회합니다.")
    @GetMapping("/paged")
    public RsData<Page<Movie>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "releaseDate"));
        Page<Movie> movies = movieContentsService.findAll(pageable);
        return new RsData<>("200", "영화 목록 조회", movies);
    }

    @Operation(summary = "영화 단건 조회", description = "영화 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{movieId}")
    public RsData<Movie> getMovieById(@PathVariable Long movieId) {
        return movieContentsService.findById(movieId)
                .map(movie -> new RsData<>("200", "영화 조회 완료", movie))
                .orElseThrow(() -> new ServiceException("404", "해당 ID의 영화가 존재하지 않습니다."));
    }

    @Operation(summary = "영화 이름 검색", description = "영화 제목을 기준으로 영화를 검색합니다.")
    @GetMapping("/search/title")
    public RsData<List<Movie>> searchByTitle(@RequestParam String title) {
        List<Movie> result = movieContentsService.findByTitle(title);
        return new RsData<>("200", "제목 검색 결과", result);
    }

    @Operation(summary = "단일 영화 장르 검색", description = "지정한 장르(문자열 검색)가 포함된 영화를 검색합니다. 한개의 장르 검색만 바랍니다.")
    @GetMapping("/search/genre")
    public RsData<List<Movie>> searchByGenre(@RequestParam String genre) {
        List<Movie> result = movieContentsService.findByGenre(genre);
        return new RsData<>("200", "장르 검색 결과", result);
    }

    @Operation(summary = "영화 수정", description = "영화 ID에 해당하는 영화를 수정합니다.")
    @PutMapping("/{movieId}")
    public RsData<Movie> updateMovie(@PathVariable Long movieId, @RequestBody Movie movie) {
        try {
            Movie updated = movieContentsService.update(movieId, movie);
            return new RsData<>("200", "영화 수정 완료", updated);
        } catch (IllegalArgumentException e) {
            throw new ServiceException("404", "해당 ID의 영화가 존재하지 않습니다.");
        }
    }

    @Operation(summary = "영화 삭제", description = "영화 ID로 영화를 삭제합니다.")
    @DeleteMapping("/{movieId}")
    public RsData<Void> deleteMovie(@PathVariable Long movieId) {
        try {
            movieContentsService.delete(movieId);
            return new RsData<>("204", "영화 삭제 완료", null);
        } catch (Exception e) {
            throw new ServiceException("404", "해당 ID의 영화가 존재하지 않습니다.");
        }
    }
}
