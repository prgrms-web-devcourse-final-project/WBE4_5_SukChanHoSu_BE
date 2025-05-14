package com.NBE4_5_SukChanHoSu.BE.domain.likes.entity;


import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "matches")
public class Matching {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchingId;

    @ManyToOne
    @JoinColumn(name = "male_user_id", nullable = false)
    @JsonIgnoreProperties({"likes", "likedBy", "maleMatchings", "femaleMatchings", "user"})
    private UserProfile maleUser; // 남자 유저

    @ManyToOne
    @JoinColumn(name = "female_user_id", nullable = false)
    @JsonIgnoreProperties({"likes", "likedBy", "maleMatchings", "femaleMatchings", "user"})
    private UserProfile femaleUser; // 두 번째 사용자

    @CreatedDate
    @Column(name = "matching_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt; // 매칭된 시간

    public Matching(UserProfile maleUser, UserProfile femaleUser) {
        this.maleUser = maleUser;
        this.femaleUser = femaleUser;
        this.createdAt = LocalDateTime.now();
    }
}
