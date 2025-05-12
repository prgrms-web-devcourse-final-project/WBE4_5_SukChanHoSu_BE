package com.NBE4_5_SukChanHoSu.BE.domain.movie.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.entity.Movie;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.repository.MovieRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.service.MovieContentsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MovieContentsServiceTest {

    @Mock
    private MovieRepository movieRepository;
    private MovieContentsService movieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        movieService = new MovieContentsService(movieRepository);
    }

    @Test
    void save_shouldReturnSavedMovie() {
        Movie movie = Movie.builder().movieId(1L).title("Test Movie").build();
        when(movieRepository.save(movie)).thenReturn(movie);

        Movie saved = movieService.save(movie);

        assertThat(saved).isEqualTo(movie);
        verify(movieRepository).save(movie);
    }

    @Test
    void findAll_shouldReturnListOfMovies() {
        List<Movie> movies = List.of(new Movie(), new Movie());
        when(movieRepository.findAll()).thenReturn(movies);

        List<Movie> result = movieService.findAll();

        assertThat(result).hasSize(2);
        verify(movieRepository).findAll();
    }

    @Test
    void findById_shouldReturnMovieIfFound() {
        Movie movie = Movie.builder().movieId(1L).build();
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        Optional<Movie> result = movieService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getMovieId()).isEqualTo(1L);
    }

    @Test
    void findByTitle_shouldCallRepositoryCorrectly() {
        String keyword = "action";
        List<Movie> mockList = List.of(new Movie());
        when(movieRepository.findByTitleContainingIgnoreCase(keyword)).thenReturn(mockList);

        List<Movie> result = movieService.findByTitle(keyword);

        assertThat(result).hasSize(1);
        verify(movieRepository).findByTitleContainingIgnoreCase(keyword);
    }

    @Test
    void findByGenre_shouldCallRepositoryCorrectly() {
        String genre = "DRAMA";
        when(movieRepository.findByGenresRawContainingIgnoreCase(genre)).thenReturn(List.of(new Movie()));

        List<Movie> result = movieService.findByGenre(genre);

        assertThat(result).hasSize(1);
        verify(movieRepository).findByGenresRawContainingIgnoreCase(genre);
    }

    @Test
    void update_shouldUpdateExistingMovie() {
        Movie existing = Movie.builder().movieId(1L).title("Old").build();
        Movie updated = Movie.builder().title("New").build();
        when(movieRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(movieRepository.save(any())).thenReturn(existing);

        Movie result = movieService.update(1L, updated);

        assertThat(result.getTitle()).isEqualTo("New");
        verify(movieRepository).save(existing);
    }

    @Test
    void delete_shouldCallDeleteById() {
        movieService.delete(1L);
        verify(movieRepository).deleteById(1L);
    }
}
