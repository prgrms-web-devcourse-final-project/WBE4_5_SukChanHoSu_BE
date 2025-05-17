package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.service;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.responseCode.ReviewErrorCode;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.request.ReviewRequestDto;
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
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private static final String LIKE_PREFIX = "like";
    private static final int FIRST_LINE = 0;
    private static final int FIRST_INDEX = 0;
    private static final int SECOND_INDEX = 1;
    private static final List<Pattern> PROFANITY_PATTERNS = List.of(
            Pattern.compile("씨발", Pattern.CASE_INSENSITIVE),
            Pattern.compile("좆", Pattern.CASE_INSENSITIVE),
            Pattern.compile("개새끼", Pattern.CASE_INSENSITIVE),
            Pattern.compile("병신", Pattern.CASE_INSENSITIVE),
            Pattern.compile("ㅅㅂ", Pattern.CASE_INSENSITIVE),
            Pattern.compile("ㅈ같", Pattern.CASE_INSENSITIVE),
            Pattern.compile("꺼져", Pattern.CASE_INSENSITIVE),
            Pattern.compile("미친", Pattern.CASE_INSENSITIVE),
            Pattern.compile("염병", Pattern.CASE_INSENSITIVE)
    );

    public ReviewResponseDto createReviewPost(ReviewRequestDto requestDto) {
        User user = SecurityUtil.getCurrentUser();

        // 욕설 마스킹 적용
        String filteredContent = maskProfanity(requestDto.getContent());
        Review review = Review.builder()
                .title(requestDto.getTitle())
                .content(filteredContent)
                .rating(requestDto.getRating())
                .user(user)
                .build();

        reviewRepository.save(review);

        return new ReviewResponseDto(review);
    }

    // initData 용 메서드
    public void initCreateReviewPost(ReviewRequestDto requestDto, User user) {

        Review review = Review.builder()
                .title(requestDto.getTitle())
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

    // todo 추후 영화 id 로 변경
    public AllReviewDto getAllReviewsByTitle(String title, String sort) {
        List<ReviewResponseDto> reviewList = new ArrayList<>();

        if (sort.isEmpty()) {
            List<Review> reviews = reviewRepository.findByTitleOrderByCreatedDateDesc(title);
            reviewList = reviews.stream()
                    .map(ReviewResponseDto::new)
                    .toList();
        }

        if (sort.equalsIgnoreCase(LIKE_PREFIX)) {
            List<Review> reviews = reviewRepository.findByTitleOrderByLikeCountDescCreatedDateDesc(title);
            reviewList = reviews.stream()
                    .map(ReviewResponseDto::new)
                    .toList();
        }

        List<Object[]> statList = reviewRepository.getReviewStatsByTitle(title);
        Object[] stats = statList.get(FIRST_LINE);

        Long count = ((Number) stats[FIRST_INDEX]).longValue();
        Double avg = ((Number) stats[SECOND_INDEX]).doubleValue();
        return new AllReviewDto(reviewList, count, avg);
    }

    @Transactional
    public void updateReview(Long reviewId, ReviewRequestDto requestDto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ServiceException(
                                ReviewErrorCode.REVIEW_NOT_FOUND.getCode(),
                                ReviewErrorCode.REVIEW_NOT_FOUND.getMessage()
                        )
                );

        if (requestDto.getContent() != null) {
            // 욕설 마스킹 적용
            String filteredContent = maskProfanity(requestDto.getContent());
            review.setContent(filteredContent);
        }

        if (requestDto.getRating() != null) {
            review.setRating(requestDto.getRating());
        }
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    // 욕설 마스킹 내부 메서드
    private String maskProfanity(String content) {
        if (content == null) return null;

        for (Pattern pattern : PROFANITY_PATTERNS) {
            content = pattern.matcher(content).replaceAll("ㅇㅇ");
        }
        return content;
    }
}