package com.NBE4_5_SukChanHoSu.BE.domain.user.entity;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.Matching;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.UserLikes;
import com.NBE4_5_SukChanHoSu.BE.global.BaseTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
@Table(name = "user_profile")
public class UserProfile extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Setter
    @Column(nullable = false)
    private String nickName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    @Setter
    private String profileImage;

    private LocalDate birthdate;

    @ElementCollection(targetClass = Genre.class)
    @Enumerated(EnumType.STRING)
    private List<Genre> favoriteGenres;

    @Column(length = 100)
    @Setter
    private String introduce;

    @Column(nullable = false)
    @Setter
    private double latitude;
    @Column(nullable = false)
    @Setter
    private double longitude;

    @Column(nullable = false)
    @Setter
    private int searchRadius = 20;

    @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"fromUser", "toUser"})
    private List<UserLikes> likes = new ArrayList<>(); // 사용자가 누른 좋아요 목록

    @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"fromUser", "toUser"})
    private List<UserLikes> likedBy = new ArrayList<>(); // 사용자를 좋아요한 목록

    @OneToMany(mappedBy = "maleUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"maleUser", "femaleUser"})
    private List<Matching> maleMatchings = new ArrayList<>(); // 매칭된 남자 사용자 목록

    @OneToMany(mappedBy = "femaleUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"maleUser", "femaleUser"})
    private List<Matching> femaleMatchings = new ArrayList<>(); // 매칭된 여자 사용자 목록

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @JsonIgnoreProperties({"userProfile"})
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(userId, that.userId); // 사용자 ID로 동등성 비교
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId); // 사용자 ID로 해시코드 생성
    }

}
