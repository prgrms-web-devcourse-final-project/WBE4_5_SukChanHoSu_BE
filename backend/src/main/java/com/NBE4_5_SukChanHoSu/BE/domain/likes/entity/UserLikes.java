package com.NBE4_5_SukChanHoSu.BE.domain.likes.entity;


import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "user_likes",
        indexes = {
                @Index(name = "idx_like_time", columnList = "like_time")
        })
public class UserLikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userLikeId;

    @ManyToOne
    @JoinColumn(name = "liker_id", nullable = false)
    @JsonIgnoreProperties({"likes", "likedBy", "maleMatchings", "femaleMatchings", "user"})
    private UserProfile fromUser;  // 좋아요를 보낸 사용자

    @ManyToOne
    @JoinColumn(name = "liked_id", nullable = false)
    @JsonIgnoreProperties({"likes", "likedBy", "maleMatchings", "femaleMatchings", "user"})
    private UserProfile toUser;  // 좋아요를 받은 사용자

    @CreatedDate
    @Column(name = "like_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public UserLikes(UserProfile fromUser, UserProfile toUser) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.createdAt = LocalDateTime.now();
    }
}
