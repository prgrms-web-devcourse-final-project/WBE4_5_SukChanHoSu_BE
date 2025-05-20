package com.NBE4_5_SukChanHoSu.BE.domain.movie.service;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.document.MovieDocument;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.entity.Movie;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.repository.MovieElasticsearchRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.repository.MovieRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieContentsService {

    private final MovieRepository movieRepository;
    private final MovieElasticsearchRepository movieElasticsearchRepository;

    // Movie → MovieDocument 변환 헬퍼
    private MovieDocument toMovieDocument(Movie movie) {
        String genresRaw = ""; // 기본값으로 빈 문자열 설정

        // movie.getGenres()가 null이 아닌 경우에만 스트림 처리
        if (movie.getGenres() != null) {
            genresRaw = movie.getGenres().stream()
                    .map(Genre::getLabel) // Genre enum에 getLabel() 메서드가 있다고 가정합니다.
                    .collect(Collectors.joining(", "));
        }
        // else: genresRaw는 초기값인 "" (빈 문자열)을 유지합니다.

        return MovieDocument.builder()
                .movieId(movie.getMovieId())
                .title(movie.getTitle())
                .genresRaw(genresRaw) // null 체크 후 처리된 genresRaw 사용
                .description(movie.getDescription())
                .director(movie.getDirector())
                .build();
    }

    public Movie save(Movie movie) {
        Movie savedMovie = movieRepository.save(movie);
        movieElasticsearchRepository.save(toMovieDocument(savedMovie));
        return savedMovie;
    }

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    public Optional<Movie> findById(Long movieId) {
        return movieRepository.findById(movieId);
    }

    public Page<Movie> findAll(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    public List<Movie> findByTitle(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Movie> findByGenre(String genreKeyword) {
        return movieRepository.findByGenresRawContainingIgnoreCase(genreKeyword);
    }

    @Transactional
    public Movie update(Long movieId, Movie updatedMovie) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found: " + movieId));

        movie.setGenres(updatedMovie.getGenres());
        movie.setTitle(updatedMovie.getTitle());
        movie.setReleaseDate(updatedMovie.getReleaseDate());
        movie.setPosterImage(updatedMovie.getPosterImage());
        movie.setDescription(updatedMovie.getDescription());
        movie.setRating(updatedMovie.getRating());
        movie.setDirector(updatedMovie.getDirector());

        Movie saved = movieRepository.save(movie);
        movieElasticsearchRepository.save(toMovieDocument(saved)); // Elasticsearch 동기화
        return saved;
    }

    @Transactional
    public void delete(Long movieId) {
        movieRepository.deleteById(movieId);
        movieElasticsearchRepository.deleteById(movieId); // Elasticsearch에서도 삭제
    }

    // Elasticsearch: 자동완성
    public List<String> autocompleteTitleFromEs(String query) {
        return movieElasticsearchRepository.findByTitleStartingWith(query)
                .stream()
                .map(MovieDocument::getTitle)
                .collect(Collectors.toList());
    }

    private Movie toMovieEntity(MovieDocument document) {
        List<Genre> genres = Genre.parseGenres(document.getGenresRaw());

        return Movie.builder()
                .movieId(document.getMovieId())
                .title(document.getTitle())
                .genres(genres)
                .description(document.getDescription())
                .director(document.getDirector())
                .build();
    }

}
