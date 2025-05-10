package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.entity.Review;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import lombok.Data;

@Data
public class ReviewResponseDto {
    private Long id;
    // todo 영화 객체로 변경 예정
    private String title;
    private String content;
    private User user;
    private Double rating;

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.title = review.getTitle();
        this.content = review.getContent();
        this.user = review.getUser();
        this.rating = review.getRating();
    }
}
