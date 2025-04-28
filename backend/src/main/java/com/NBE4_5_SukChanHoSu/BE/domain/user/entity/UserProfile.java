package com.NBE4_5_SukChanHoSu.BE.domain.user.entity;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.Matching;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.UserLikes;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_profile")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String nickName;
    @Column(nullable = false)
    private String gender;
    @Column(nullable = false)
    private String profileImage;

    @ElementCollection(targetClass = Genre.class)
    @Enumerated(EnumType.STRING)
    private List<Genre> favoriteGenres;

    @Column(length = 100)
    private String introduce;

    @Column(nullable = false)
    private double latitude;
    @Column(nullable = false)
    private double longitude;

    @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<UserLikes> likes = new ArrayList<>(); // 사용자가 누른 좋아요 목록

    @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<UserLikes> likedBy = new ArrayList<>(); // 사용자를 좋아요한 목록

    @OneToMany(mappedBy = "user1", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Matching> matchings = new ArrayList<>(); // 매칭된 사용자 목록

}
