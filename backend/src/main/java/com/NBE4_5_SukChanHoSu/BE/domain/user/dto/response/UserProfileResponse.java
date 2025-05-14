package com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Gender;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class UserProfileResponse {
    private Long userId;
    private String nickName;
    private Gender gender;
    private String profileImage;
    private LocalDate birthdate;
    private List<Genre> favoriteGenres;
    private String introduce;
    private double latitude;
    private double longitude;
    private int searchRadius;
    private String distance;
    private LocalDateTime time;

    public UserProfileResponse (UserProfile userProfile, int distance, LocalDateTime time) {
        this.userId = userProfile.getUserId();
        this.nickName = userProfile.getNickName();
        this.gender = userProfile.getGender();
        this.profileImage = userProfile.getProfileImage();
        this.birthdate = userProfile.getBirthdate();
        this.favoriteGenres = userProfile.getFavoriteGenres();
        this.introduce = userProfile.getIntroduce();
        this.latitude = userProfile.getLatitude();
        this.longitude = userProfile.getLongitude();
        this.searchRadius = userProfile.getSearchRadius();
        this.time = time;
        this.distance = "약 "+distance+"km";
    }

    public UserProfileResponse (UserProfile userProfile, int distance) {
        this.userId = userProfile.getUserId();
        this.nickName = userProfile.getNickName();
        this.gender = userProfile.getGender();
        this.profileImage = userProfile.getProfileImage();
        this.birthdate = userProfile.getBirthdate();
        this.favoriteGenres = userProfile.getFavoriteGenres();
        this.introduce = userProfile.getIntroduce();
        this.latitude = userProfile.getLatitude();
        this.longitude = userProfile.getLongitude();
        this.searchRadius = userProfile.getSearchRadius();
        this.distance = "약 "+distance+"km";
    }

}
