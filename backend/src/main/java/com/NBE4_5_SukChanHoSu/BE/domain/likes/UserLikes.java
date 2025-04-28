package com.NBE4_5_SukChanHoSu.BE.domain.likes;


import com.NBE4_5_SukChanHoSu.BE.domain.user.UserProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_likes")
public class UserLikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userLikeId;

    @ManyToOne
    @JoinColumn(name = "liker_id", nullable = false)
    private UserProfile fromUser;  // 좋아요를 보낸 사용자

    @ManyToOne
    @JoinColumn(name = "liked_id", nullable = false)
    private UserProfile toUser;  // 좋아요를 받은 사용자

    @Column(name = "like_time",nullable = false)
    private LocalDateTime likeTime;

}
