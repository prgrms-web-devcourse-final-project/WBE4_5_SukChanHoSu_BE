package com.NBE4_5_SukChanHoSu.BE.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class RecommendUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long recommendedUserId;
    private String type; // 추천 타입 ("tags", "distance")
    private LocalDateTime createdAt;

    public RecommendUser(Long userId, Long recommendedUserId, String type) {
        this.userId = userId;
        this.recommendedUserId = recommendedUserId;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

}