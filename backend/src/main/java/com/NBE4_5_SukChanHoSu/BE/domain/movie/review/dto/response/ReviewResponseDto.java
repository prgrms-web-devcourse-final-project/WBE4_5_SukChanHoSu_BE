package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.dto.response;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.review.entity.Review;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private String title;
    private String content;
    private String userName;
    private int likeCount;
    private LocalDateTime createdAt;
    private Double rating;

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.title = review.getMovie().getTitle();
        this.content = review.getContent();
        this.userName = review.getUser().getUserProfile().getNickName();
        this.likeCount = review.getLikeCount();
        this.createdAt = review.getCreatedDate();
        this.rating = review.getRating();
    }
}