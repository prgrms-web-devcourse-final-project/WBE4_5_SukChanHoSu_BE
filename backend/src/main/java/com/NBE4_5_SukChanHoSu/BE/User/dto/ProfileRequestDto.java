package com.NBE4_5_SukChanHoSu.BE.User.dto;

import com.NBE4_5_SukChanHoSu.BE.User.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileRequestDto {

    @NotBlank
    @Pattern(regexp = "^[가-힣a-zA-Z]{2,10}$", message = "닉네임은 한글 또는 영어 2~10자만 가능합니다.")
    private String nickname;

    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;

    private Gender gender;

    private String profileImage;

    @DecimalMin(value = "-90.0", inclusive = true, message = "위도는 -90 ~ 90 사이여야 합니다.")
    @DecimalMax(value = "90.0", inclusive = true, message = "위도는 -90 ~ 90 사이여야 합니다.")
    private Double latitude;

    @DecimalMin(value = "-180.0", inclusive = true, message = "경도는 -180 ~ 180 사이여야 합니다.")
    @DecimalMax(value = "180.0", inclusive = true, message = "경도는 -180 ~ 180 사이여야 합니다.")
    private Double longitude;

    private LocalDate birthdate;

    @Min(0)
    @Max(50)
    private Integer distance; // 상대방과의 허용 거리 (km)

    @NotBlank(message = "인생 영화는 필수입니다.")
    private String lifeMovie;

    private List<String> favoriteGenres; // 선호 장르

    @Size(max = 4, message = "재밌게 본 영화는 최대 4개까지 등록 가능합니다.")
    private List<String> watchedMovies;

    private List<String> preferredTheaters; // 선호 영화관

}
