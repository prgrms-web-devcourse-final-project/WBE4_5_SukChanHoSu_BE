package com.NBE4_5_SukChanHoSu.BE.User.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class UserProfileDto {

    @NotBlank
    @Pattern(regexp = "^[가-힣a-zA-Z]{2,10}$", message = "닉네임은 한글 또는 영어 2~10자만 가능합니다.")
    private String nickname;

    @Email
    private String email;

    @NotBlank
    private String gender;

    private String profileImage;

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;

    private LocalDate birthdate;
}


