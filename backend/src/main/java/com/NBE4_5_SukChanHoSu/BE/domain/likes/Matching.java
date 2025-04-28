package com.NBE4_5_SukChanHoSu.BE.domain.likes;


import com.NBE4_5_SukChanHoSu.BE.domain.user.UserProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "matches")
public class Matching {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_one_id", nullable = false)
    private UserProfile user1; // 첫 번째 사용자

    @ManyToOne
    @JoinColumn(name = "user_two_id", nullable = false)
    private UserProfile user2; // 두 번째 사용자

    @Column(name = "match_time", nullable = false)
    private java.util.Date matchTime; // 매칭된 시간
}
