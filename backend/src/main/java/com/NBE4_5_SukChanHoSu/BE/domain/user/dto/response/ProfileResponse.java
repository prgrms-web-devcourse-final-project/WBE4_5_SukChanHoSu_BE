//package com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response;
//
//import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Gender;
//import jakarta.validation.constraints.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//public class ProfileResponse {
//
//    @NotBlank
//    @Pattern(regexp = "^[가-힣a-zA-Z]{2,10}$", message = "닉네임은 한글 또는 영어 2~10자만 가능합니다.")
//    private String nickname;
//
//    @Email
//    private String email;
//
//    @NotBlank
//    private Gender gender;
//
//    private String profileImage;
//
//    @DecimalMin("-90.0")
//    @DecimalMax("90.0")
//    private Double latitude;
//
//    @DecimalMin("-180.0")
//    @DecimalMax("180.0")
//    private Double longitude;
//
//    private LocalDate birthdate;
//
//    @Min(0)
//    @Max(50)
//    private Integer distance; // 상대방과의 허용 거리 (km)
//
//    @NotBlank(message = "인생 영화는 필수입니다.")
//    private String lifeMovie;
//
//    private List<String> favoriteGenres; // 선호 장르
//
//    @Size(max = 4, message = "재밌게 본 영화는 최대 4개까지 등록 가능합니다.")
//    private List<String> watchedMovies;
//
//    private List<String> preferredTheaters; // 선호 영화관
//
//    @NotBlank(message = "자기소개는 필수입니다.")
//    private String introduce;
//
//}
package com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Gender;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile; // UserProfile import
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ProfileResponse {

    private String nickname;
    private String email;
    private Gender gender;
    private String profileImage;
    private Double latitude;
    private Double longitude;
    private LocalDate birthdate;
    private Integer distance;
    private String lifeMovie;
    private List<String> favoriteGenres;
    private List<String> watchedMovies;
    private List<String> preferredTheaters;
    private String introduce;

    public ProfileResponse(UserProfile userProfile) {
        this.nickname = userProfile.getNickName();
        this.email = userProfile.getUser().getEmail();
        this.gender = userProfile.getGender();
        this.profileImage = userProfile.getProfileImage();
        this.latitude = userProfile.getLatitude();
        this.longitude = userProfile.getLongitude();
        this.birthdate = userProfile.getBirthdate();
        this.distance = userProfile.getSearchRadius();
        this.lifeMovie = userProfile.getLifeMovie();
        this.favoriteGenres = userProfile.getFavoriteGenres() != null ? userProfile.getFavoriteGenres().stream().map(Enum::name).collect(Collectors.toList()) : null;
        this.watchedMovies = userProfile.getWatchedMovies();
        this.preferredTheaters = userProfile.getPreferredTheaters();
        this.introduce = userProfile.getIntroduce();
    }
}
