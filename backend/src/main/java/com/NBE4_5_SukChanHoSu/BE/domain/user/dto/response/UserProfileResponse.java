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
    private List<String> profileImages;
    private List<Genre> favoriteGenres;
    private String introduce;
    private String distance;
    private LocalDateTime createdAt;

    public UserProfileResponse (UserProfile userProfile, int distance, LocalDateTime createdAt) {
        this.userId = userProfile.getUserId();
        this.nickName = userProfile.getNickName();
        this.profileImages = userProfile.getProfileImages();
        this.favoriteGenres = userProfile.getFavoriteGenres();
        this.introduce = userProfile.getIntroduce();
        this.createdAt = createdAt;
        this.distance = "약 "+distance+"km";
    }

    public UserProfileResponse (UserProfile userProfile, int distance) {
        this.userId = userProfile.getUserId();
        this.nickName = userProfile.getNickName();
        this.profileImages = userProfile.getProfileImages();
        this.favoriteGenres = userProfile.getFavoriteGenres();
        this.introduce = userProfile.getIntroduce();
        this.distance = "약 "+distance+"km";
    }
}
