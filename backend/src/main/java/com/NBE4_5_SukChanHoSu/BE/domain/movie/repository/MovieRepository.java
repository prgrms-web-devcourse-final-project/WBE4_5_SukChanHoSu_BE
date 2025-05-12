package com.NBE4_5_SukChanHoSu.BE.domain.movie.repository;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByTitleContainingIgnoreCase(String title);

    List<Movie> findByGenresRawContainingIgnoreCase(String genre);
}