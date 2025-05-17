package com.NBE4_5_SukChanHoSu.BE.domain.movie.review.entity;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.global.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class Review extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    // todo 영화 객체로 변경
    private String title;
    private String content;
    private int likeCount;
    private Double rating;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    public void setContent(String content) {
        this.content = content;
        this.filterProfanity();
    }

    private void filterProfanity() {
        // 내부에서 필터링하여 저장
        this.content = this.content.replaceAll("씨발|좆|개새끼|병신|ㅅㅂ|ㅈ같|꺼져|미친|염병", "ㅇㅇ");
    }

}