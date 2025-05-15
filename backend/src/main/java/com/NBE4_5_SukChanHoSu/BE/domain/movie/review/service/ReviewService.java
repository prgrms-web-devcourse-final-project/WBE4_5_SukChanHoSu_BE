package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.service;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.entity.Movie;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.repository.MovieRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.constant.ReviewErrorCode;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.request.ReviewCreateDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.request.ReviewUpdateDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response.AllReviewDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response.ReviewResponseDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.entity.Review;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.repository.ReviewRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.global.exception.ServiceException;
import com.NBE4_5_SukChanHoSu.BE.global.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;

    private static final String LIKE_PREFIX = "like";
    private static final int FIRST_LINE = 0;
    private static final int FIRST_INDEX = 0;
    private static final int SECOND_INDEX = 1;

    public ReviewResponseDto createReviewPost(ReviewCreateDto requestDto) {
        User user = SecurityUtil.getCurrentUser();
        Movie movie = movieRepository.getReferenceById(requestDto.getMovieId());

        Review review = Review.builder()
                .movie(movie)
                .content(requestDto.getContent())
                .rating(requestDto.getRating())
                .user(user)
                .build();

        reviewRepository.save(review);

        return new ReviewResponseDto(review);
    }

    // initData 용 메서드
    public void initCreateReviewPost(ReviewCreateDto requestDto, User user) {
        Movie movie = movieRepository.getReferenceById(requestDto.getMovieId());

        Review review = Review.builder()
                .movie(movie)
                .content(requestDto.getContent())
                .rating(requestDto.getRating())
                .user(user)
                .build();

        reviewRepository.save(review);

        new ReviewResponseDto(review);
    }

    public ReviewResponseDto getOneReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ServiceException(
                                ReviewErrorCode.REVIEW_NOT_FOUND.getCode(),
                                ReviewErrorCode.REVIEW_NOT_FOUND.getMessage()
                        )
                );
        return new ReviewResponseDto(review);
    }

    public AllReviewDto getAllReviewsByMovieId(Long movieId, String sort) {
        List<ReviewResponseDto> reviewList = new ArrayList<>();

        if (sort.isEmpty()) {
            List<Review> reviews = reviewRepository.findByMovie_MovieIdOrderByCreatedDateDesc(movieId);
            reviewList = reviews.stream()
                    .map(ReviewResponseDto::new)
                    .toList();
        }

        if (sort.equalsIgnoreCase(LIKE_PREFIX)) {
            List<Review> reviews = reviewRepository.findByMovie_MovieIdOrderByLikeCountDescCreatedDateDesc(movieId);
            reviewList = reviews.stream()
                    .map(ReviewResponseDto::new)
                    .toList();
        }

        List<Object[]> statList = reviewRepository.getReviewStatsByMovie(movieId);
        Object[] stats = statList.get(FIRST_LINE);

        Long count = ((Number) stats[FIRST_INDEX]).longValue();
        Double avg = ((Number) stats[SECOND_INDEX]).doubleValue();
        return new AllReviewDto(reviewList, count, avg);
    }

    @Transactional
    public void updateReview(Long reviewId, ReviewUpdateDto requestDto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ServiceException(
                                ReviewErrorCode.REVIEW_NOT_FOUND.getCode(),
                                ReviewErrorCode.REVIEW_NOT_FOUND.getMessage()
                        )
                );

        if (requestDto.getContent() != null) {
            review.setContent(requestDto.getContent());
        }

        if (requestDto.getRating() != null) {
            review.setRating(requestDto.getRating());
        }
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}