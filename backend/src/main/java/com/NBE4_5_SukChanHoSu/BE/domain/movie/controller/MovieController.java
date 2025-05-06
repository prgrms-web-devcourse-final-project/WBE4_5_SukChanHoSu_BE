package com.NBE4_5_SukChanHoSu.BE.domain.movie.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.Movie;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.MovieService;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.UpdateMovieDto;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "영화 API", description = "영화 CRUD 및 조회 관련 기능 제공")
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    @Operation(summary = "영화 등록", description = "새로운 영화를 등록합니다.")
    public RsData<Movie> createMovie(@RequestBody Movie movie) {
        Movie saved = movieService.save(movie);
        return new RsData<>("201-CREATED", "영화가 등록되었습니다.", saved);
    }

    @GetMapping
    @Operation(summary = "영화 전체 조회", description = "등록된 모든 영화를 조회합니다.")
    public RsData<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.getAll();
        return new RsData<>("200-OK", "영화 목록 조회 성공", movies);
    }

    @GetMapping("/{id}")
    @Operation(summary = "영화 단건 조회", description = "ID로 특정 영화를 조회합니다.")
    public RsData<Movie> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getById(id);
        return new RsData<>("200-OK", "영화 조회 성공", movie);
    }

    @PutMapping("/{id}")
    @Operation(summary = "영화 수정", description = "ID에 해당하는 영화 정보를 수정합니다.")
    public RsData<Movie> updateMovie(@PathVariable Long id, @RequestBody UpdateMovieDto dto) {
        Movie updated = movieService.update(id, dto);
        return new RsData<>("200-UPDATED", "영화가 수정되었습니다.", updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "영화 삭제", description = "ID에 해당하는 영화를 삭제합니다.")
    public RsData<?> deleteMovie(@PathVariable Long id) {
        movieService.delete(id);
        return new RsData<>("200-DELETED", "영화가 삭제되었습니다.");
    }
}