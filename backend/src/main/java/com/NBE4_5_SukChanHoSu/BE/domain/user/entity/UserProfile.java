package com.NBE4_5_SukChanHoSu.BE.domain.user.entity;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.Matching;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.UserLikes;
import com.NBE4_5_SukChanHoSu.BE.global.BaseTime;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Table(name = "user_profile")
public class UserProfile extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,30}$", message = "닉네임은 한글, 영어 또는 숫자 2~30자만 가능합니다.")
    @Column(nullable = false)
    private String nickName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private String profileImage;

    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate birthdate;

    @ElementCollection(targetClass = Genre.class)
    @Enumerated(EnumType.STRING)
    @Size(max = 5, message = "선호 장르는 최대 5개까지만 등록할 수 있습니다.")
    private List<Genre> favoriteGenres;

    @NotBlank
    @Size(max = 100, message = "자기소개는 최대 100자까지 가능합니다.")
    @Column(length = 100)
    private String introduce;

    @DecimalMin(value = "-90.0", inclusive = true, message = "위도는 -90 ~ 90 사이여야 합니다.")
    @DecimalMax(value = "90.0", inclusive = true, message = "위도는 -90 ~ 90 사이여야 합니다.")
    @Column(nullable = false)
    private double latitude;

    @DecimalMin(value = "-180.0", inclusive = true, message = "경도는 -180 ~ 180 사이여야 합니다.")
    @DecimalMax(value = "180.0", inclusive = true, message = "경도는 -180 ~ 180 사이여야 합니다.")
    @Column(nullable = false)
    private double longitude;

    @Min(value = 0, message = "허용 반경은 0km 이상이어야 합니다.")
    @Max(value = 50, message = "허용 반경은 최대 50km까지만 설정할 수 있습니다.")
    @Column(nullable = false)
    private int searchRadius;

    @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<UserLikes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<UserLikes> likedBy = new ArrayList<>();

    @OneToMany(mappedBy = "maleUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Matching> maleMatchings = new ArrayList<>();

    @OneToMany(mappedBy = "femaleUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Matching> femaleMatchings = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    // 인생 영화
//    @NotBlank
//    @Column(nullable = false)
    private String lifeMovie;

    // 재밌게 본 영화
    @ElementCollection
    @CollectionTable(name = "watched_movies", joinColumns = @JoinColumn(name = "user_id"))
    private List<String> watchedMovies = new ArrayList<>();

    // 선호 영화관
    @ElementCollection
    @CollectionTable(name = "preferred_theaters", joinColumns = @JoinColumn(name = "user_id"))
    private List<String> preferredTheaters = new ArrayList<>();

}
