package com.NBE4_5_SukChanHoSu.BE.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private List<Genre> favouriteGenres;

    @Column(length = 100)
    private String introduce;

    @Column(nullable = false)
    private double latitude;
    @Column(nullable = false)
    private double longitude;

}
