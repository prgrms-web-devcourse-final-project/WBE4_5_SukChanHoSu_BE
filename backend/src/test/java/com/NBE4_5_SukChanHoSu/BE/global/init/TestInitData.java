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
                UserSignUpRequest signUpDto = UserSignUpRequest.builder()
                        .email("testUser" + i + "@example.com")
                        .password("testPassword123!")
                        .passwordConfirm("testPassword123!")
                        .build();

                User user = userService.join(signUpDto);
                userRepository.save(user); // 저장
                userRepository.flush(); // 갱신

                UserProfile userProfile = UserProfile.builder()
                        .nickName("TempUser" + i)
                        .gender(i % 2 == 0 ? Gender.Female : Gender.Male)
                        .profileImage("https://example.com/profile" + i + ".jpg")
                        .favoriteGenres(List.of(Genre.ACTION, Genre.COMEDY, Genre.DRAMA)) // 장르 리스트 설정
                        .introduce("안녕하세요! 임시 유저 " + i + "입니다.")
                        .latitude(37.5665 + (i * 0.03)) // 임의의 위도 값
                        .longitude(126.9780 + (i * 0.03)) // 임의의 경도 값
                        .user(user) // 유저와 매핑
                        .build();

                userProfileRepository.save(userProfile);
            }
        };
    }
}
