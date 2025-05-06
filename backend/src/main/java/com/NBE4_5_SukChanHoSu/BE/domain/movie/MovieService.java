package com.NBE4_5_SukChanHoSu.BE.domain.movie;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    @Transactional
    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    public Movie getById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with id: " + id));
    }

    public List<Movie> getAll() {
        return movieRepository.findAll();
    }

    @Transactional
    public Movie update(Long id, Movie updatedMovie) {
        Movie movie = getById(id); // 존재 확인
        movie.setTitle(updatedMovie.getTitle());
        movie.setReleaseYear(updatedMovie.getReleaseYear());
        movie.setRating(updatedMovie.getRating());
        movie.setTId(updatedMovie.getTId());
        movie.setPosterImage(updatedMovie.getPosterImage());
        movie.setDescription(updatedMovie.getDescription());
        movie.setDirector(updatedMovie.getDirector());
        movie.setGenres(updatedMovie.getGenres());
        return movie;
    }

    @Transactional
    public void delete(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new EntityNotFoundException("Movie not found with id: " + id);
        }
        movieRepository.deleteById(id);
    }
}