package com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Gender;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
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
public class ProfileUpdateRequest {

    @NotBlank
    @Pattern(regexp = "^[가-힣a-zA-Z]{2,10}$", message = "닉네임은 한글 또는 영어 2~10자만 가능합니다.")
    private String nickname;

    private Gender gender;

    private String profileImage;

    @DecimalMin(value = "-90.0", inclusive = true)
    @DecimalMax(value = "90.0", inclusive = true)
    private Double latitude;

    @DecimalMin(value = "-180.0", inclusive = true)
    @DecimalMax(value = "180.0", inclusive = true)
    private Double longitude;

    private LocalDate birthdate;

    @Min(0)
    @Max(50)
    private Integer distance;

    @NotBlank
    private String lifeMovie;

    private List<Genre> favoriteGenres;

    @Size(max = 4)
    private List<String> watchedMovies;

    private List<String> preferredTheaters;

    @NotBlank(message = "자기소개는 필수입니다.")
    private String introduce;

}
