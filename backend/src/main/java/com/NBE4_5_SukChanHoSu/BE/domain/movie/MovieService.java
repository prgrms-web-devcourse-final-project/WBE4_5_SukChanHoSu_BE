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
    public Movie update(Long id, UpdateMovieDto dto) {
        Movie movie = getById(id); // 영속 상태 객체
        dto.applyTo(movie);        // 값만 덮어쓰기
        return movie;              // save() 불필요 (dirty checking)
    }

    @Transactional
    public void delete(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new EntityNotFoundException("Movie not found with id: " + id);
        }
        movieRepository.deleteById(id);
    }
}