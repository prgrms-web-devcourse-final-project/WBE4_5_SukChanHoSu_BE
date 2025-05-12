package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import static com.NBE4_5_SukChanHoSu.BE.domain.movie.review.constant.ReviewConstant.MAX_RATING;
import static com.NBE4_5_SukChanHoSu.BE.domain.movie.review.constant.ReviewConstant.MAX_RATING_MESSAGE;
import static com.NBE4_5_SukChanHoSu.BE.domain.movie.review.constant.ReviewConstant.MIN_RATING;
import static com.NBE4_5_SukChanHoSu.BE.domain.movie.review.constant.ReviewConstant.MIN_RATING_MESSAGE;

@Data
public class ReviewRequestDto {
    // todo 영화 객체로 변경 예정 (영화 id로)
    private String title;
    private String content;

    @Min(value = MIN_RATING, message = MIN_RATING_MESSAGE)
    @Max(value = MAX_RATING, message = MAX_RATING_MESSAGE)
    private Double rating;
}