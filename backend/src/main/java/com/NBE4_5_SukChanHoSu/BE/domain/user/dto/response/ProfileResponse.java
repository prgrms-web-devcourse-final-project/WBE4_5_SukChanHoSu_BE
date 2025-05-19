package com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.dto.response.MovieDto;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Gender;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile; // UserProfile import
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ProfileResponse {

    private String nickname;
    private String email;
    private Gender gender;
    private List<String> profileImages;
    private Double latitude;
    private Double longitude;
    private LocalDate birthdate;
    private int searchRadius;
    private MovieDto lifeMovie;
    private List<String> favoriteGenres;
    private List<MovieDto> watchedMovies;
    private List<String> preferredTheaters;
    private String introduce;

    public ProfileResponse(UserProfile userProfile) {
        this.nickname = userProfile.getNickName();
        this.email = userProfile.getUser().getEmail();
        this.gender = userProfile.getGender();
        this.profileImages = userProfile.getProfileImages();
        this.latitude = userProfile.getLatitude();
        this.longitude = userProfile.getLongitude();
        this.birthdate = userProfile.getBirthdate();
        this.searchRadius = userProfile.getSearchRadius();
        this.lifeMovie = userProfile.getLifeMovie() != null ? MovieDto.from(userProfile.getLifeMovie()) : null;
        this.favoriteGenres = userProfile.getFavoriteGenres() != null ? userProfile.getFavoriteGenres().stream().map(Enum::name).collect(Collectors.toList()) : null;
        this.watchedMovies = userProfile.getWatchedMovies().stream()
                .map(MovieDto::from).collect(Collectors.toList());
        this.preferredTheaters = userProfile.getPreferredTheaters();
        this.introduce = userProfile.getIntroduce();
    }
}
