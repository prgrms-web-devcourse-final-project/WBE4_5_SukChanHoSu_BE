package com.NBE4_5_SukChanHoSu.BE.domain.movie.service;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.document.MovieDocument;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.entity.Movie;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.repository.MovieElasticsearchRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.repository.MovieRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    public Movie save(Movie movie) {
        Movie savedMovie = movieRepository.save(movie);
        movieElasticsearchRepository.save(modelMapper.map(savedMovie, MovieDocument.class));
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

        return movieRepository.save(movie);
    }

    @Transactional
    public void delete(Long movieId) {
        movieRepository.deleteById(movieId);
    }

    // 엘라스틱서치 관련 메서드
    public List<Movie> searchByTitleFromEs(String title) {
        return movieElasticsearchRepository.findByTitleContaining(title)
                .stream()
                .map(document -> modelMapper.map(document, Movie.class))
                .collect(Collectors.toList());
    }

    public List<String> autocompleteTitleFromEs(String query) {
        return movieElasticsearchRepository.findByTitleStartingWith(query)
                .stream()
                .map(MovieDocument::getTitle)
                .collect(Collectors.toList());
    }
}
