package com.NBE4_5_SukChanHoSu.BE.domain.likes;


import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "matches")
public class Matching {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchingId;

    @ManyToOne
    @JoinColumn(name = "male_user_id", nullable = false)
    @JsonBackReference
    private UserProfile maleUser; // 남자 유저

    @ManyToOne
    @JoinColumn(name = "female_user_id", nullable = false)
    @JsonBackReference
    private UserProfile femaleUser; // 두 번째 사용자

    @Column(name = "matching_time", nullable = false)
    private LocalDateTime matchingTime; // 매칭된 시간
}
