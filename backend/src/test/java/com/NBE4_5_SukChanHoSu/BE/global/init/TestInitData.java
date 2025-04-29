package com.NBE4_5_SukChanHoSu.BE.global.init;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Gender;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserProfileRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@Configuration
@ActiveProfiles("test")
public class TestInitData {

    @Bean
    public ApplicationRunner initData(UserProfileRepository userProfileRepository) {
        return args -> {
            // 테스트 데이터 생성
            for (int i = 1; i <= 10; i++) {
                UserProfile userProfile = new UserProfile();
                userProfile.setNickName("TestUser" + i);
                userProfile.setGender(i % 2 == 0 ? Gender.Female : Gender.Male);
                userProfile.setProfileImage("https://example.com/test_profile" + i + ".jpg");
                // 여기에 장르를 설정합니다 (임의로 Action, Comedy, Drama 선택)
                List<Genre> genres = List.of(Genre.ACTION, Genre.COMEDY, Genre.DRAMA);
                userProfile.setFavoriteGenres(genres);
                userProfile.setIntroduce("안녕하세요! 테스트 유저 " + i + "입니다.");
                userProfile.setLatitude(37.5665 + (i * 0.01));
                userProfile.setLongitude(126.9780 + (i * 0.01));

                userProfileRepository.save(userProfile);
            }
        };
    }
}
