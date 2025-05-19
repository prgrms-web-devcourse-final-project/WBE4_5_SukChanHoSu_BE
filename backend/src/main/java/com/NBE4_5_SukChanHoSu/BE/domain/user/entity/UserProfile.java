package com.NBE4_5_SukChanHoSu.BE.domain.user.entity;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.entity.Matching;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.entity.UserLikes;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.entity.Movie;
import com.NBE4_5_SukChanHoSu.BE.global.BaseTime;
import com.NBE4_5_SukChanHoSu.BE.global.config.GenreListDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    private Long userId;

    @Setter
    @NotBlank
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,30}$", message = "닉네임은 한글, 영어 또는 숫자 2~30자만 가능합니다.")
    @Column(nullable = false, unique = true)
    private String nickName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @ElementCollection
    @Column(nullable = false)
    @Setter
    private List<String> profileImages = new ArrayList<>();

    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate birthdate;

    @ElementCollection(targetClass = Genre.class)
    @Enumerated(EnumType.STRING)
    @Size(max = 5, message = "선호 장르는 최대 5개까지만 등록할 수 있습니다.")
    @JsonDeserialize(using = GenreListDeserializer.class)
    private List<Genre> favoriteGenres;

    @NotBlank
    @Size(max = 100, message = "자기소개는 최대 100자까지 가능합니다.")
    @Column(length = 100)
    @Setter
    private String introduce;

    @DecimalMin(value = "-90.0", inclusive = true, message = "위도는 -90 ~ 90 사이여야 합니다.")
    @DecimalMax(value = "90.0", inclusive = true, message = "위도는 -90 ~ 90 사이여야 합니다.")
    @Column(nullable = false)
    @Setter
    private double latitude;

    @DecimalMin(value = "-180.0", inclusive = true, message = "경도는 -180 ~ 180 사이여야 합니다.")
    @DecimalMax(value = "180.0", inclusive = true, message = "경도는 -180 ~ 180 사이여야 합니다.")
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
    @MapsId
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

    // 인생 영화
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "life_movie_id")
    private Movie lifeMovie;

    // 재밌게 본 영화
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_watched_movie",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    private List<Movie> watchedMovies = new ArrayList<>();

    // 선호 영화관
    @ElementCollection
    @CollectionTable(name = "preferred_theaters", joinColumns = @JoinColumn(name = "user_id"))
    private List<String> preferredTheaters = new ArrayList<>();

}
