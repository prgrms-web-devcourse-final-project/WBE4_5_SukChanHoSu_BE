package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import static com.NBE4_5_SukChanHoSu.BE.domain.movie.review.constant.ReviewConstant.MAX_RATING;
import static com.NBE4_5_SukChanHoSu.BE.domain.movie.review.constant.ReviewConstant.MAX_RATING_MESSAGE;
import static com.NBE4_5_SukChanHoSu.BE.domain.movie.review.constant.ReviewConstant.MIN_RATING;
import static com.NBE4_5_SukChanHoSu.BE.domain.movie.review.constant.ReviewConstant.MIN_RATING_MESSAGE;

@Data
public class ReviewUpdateDto {
    @Min(value = MIN_RATING, message = MIN_RATING_MESSAGE)
    @Max(value = MAX_RATING, message = MAX_RATING_MESSAGE)
    private Double rating;
    private String content;
}
