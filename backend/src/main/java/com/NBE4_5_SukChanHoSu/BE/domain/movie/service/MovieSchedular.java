package com.NBE4_5_SukChanHoSu.BE.domain.movie.service;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.entity.Movie;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.repository.MovieRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.entity.Review;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieSchedular {

    @Autowired
    ReviewRepository reviewRepository;

    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE)
    public void updateReview() {
        List<Movie> movieList = reviewRepository.findDistinctMovies(); // DISTINCT movie
        List<Review> reviews = reviewRepository.findAllWithMovie(); // 전체 리뷰 가져오기

        movieList.forEach(movie -> {
            List<Review> reviewsForMovie = reviews.stream()
                    .filter(r -> r.getMovie().getMovieId().equals(movie.getMovieId()))
                    .toList();

            Double avgRating = reviewsForMovie.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);

            movie.setRating(String.format("%.2f", avgRating)); // ⛳️ Double 그대로 대입
        });
    }
}
