package com.NBE4_5_SukChanHoSu.BE.global.init;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserSignUpRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.*;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserProfileRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@Configuration
@ActiveProfiles("test")
public class TestInitData {

    @Bean
    public ApplicationRunner initData(UserProfileRepository userProfileRepository, UserService userService,UserRepository userRepository) {
        return args -> {
            // 테스트 데이터 생성
            for (int i = 1; i <= 10; i++) {
                UserSignUpRequest signUpDto = new UserSignUpRequest();
                signUpDto.setEmail("testUser" + i + "@example.com");
                signUpDto.setPassword("testPassword123!");
                signUpDto.setPasswordConfirm("testPassword123!");
                User user =userService.join(signUpDto);

                userRepository.save(user); // 저장
                userRepository.flush(); // 갱신

                UserProfile userProfile = new UserProfile();
                userProfile.setNickName("TestUser" + i);
                userProfile.setGender(i % 2 == 0 ? Gender.Female : Gender.Male);
                userProfile.setProfileImage("https://example.com/test_profile" + i + ".jpg");
                // 여기에 장르를 설정합니다 (임의로 Action, Comedy, Drama 선택)
                List<Genre> genres = List.of(Genre.ACTION, Genre.COMEDY, Genre.DRAMA);
                userProfile.setFavoriteGenres(genres);
                userProfile.setIntroduce("안녕하세요! 테스트 유저 " + i + "입니다.");
                userProfile.setLatitude(37.5665 + (i * 0.03));
                userProfile.setLongitude(126.9780 + (i * 0.03));
                userProfile.setSearchRadius(20);
                userProfile.setUser(user); // 유저와 매핑

                userProfileRepository.save(userProfile);
            }
        };
    }
}
