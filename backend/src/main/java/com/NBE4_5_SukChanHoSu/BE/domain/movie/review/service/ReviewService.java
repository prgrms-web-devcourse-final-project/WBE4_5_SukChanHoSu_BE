package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.service;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.request.ReviewRequestDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response.AllReviewDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response.ReviewResponseDto;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.entity.Review;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.repository.ReviewRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewResponseDto createReviewPost(ReviewRequestDto requestDto) {
        User user = SecurityUtil.getCurrentUser();

        Review review = Review.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .rating(requestDto.getRating())
                .user(user)
                .build();

        reviewRepository.save(review);

        return new ReviewResponseDto(review);
    }

    // initData 용 메서드
    public ReviewResponseDto initCreateReviewPost(ReviewRequestDto requestDto, User user) {

        Review review = Review.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .rating(requestDto.getRating())
                .user(user)
                .build();

        reviewRepository.save(review);

        return new ReviewResponseDto(review);
    }

    public ReviewResponseDto getOneReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("null"));
        return new ReviewResponseDto(review);
    }

    // todo 추후 영화 id 로 변경
    public AllReviewDto getAllReviewsByTitle(String title) {
        List<Review> reviews = reviewRepository.findByTitle(title);
        List<ReviewResponseDto> reviewList = reviews.stream()
                .map(ReviewResponseDto::new)
                .toList();
        List<Object[]> statList = reviewRepository.getReviewStats(title);
        Object[] stats = statList.getFirst();

        Long count = ((Number) stats[0]).longValue();
        Double avg = ((Number) stats[1]).doubleValue();
        return new AllReviewDto(reviewList, count, avg);
    }

    public void updateReview(Long reviewId, ReviewRequestDto requestDto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("null"));
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
