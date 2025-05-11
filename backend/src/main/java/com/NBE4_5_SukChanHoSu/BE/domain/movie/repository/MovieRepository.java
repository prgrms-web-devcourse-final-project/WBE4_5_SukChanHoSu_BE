package com.NBE4_5_SukChanHoSu.BE.domain.movie.repository;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {

}