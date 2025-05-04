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
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@ActiveProfiles("test")
public class TestInitData {

    @Bean
    public ApplicationRunner initData(UserProfileRepository userProfileRepository, UserService userService,UserRepository userRepository) {
        Random random = new Random();
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

                // 랜덤 장르 3개 선택
                List<Genre> genres = Stream.of(Genre.values())
                        .sorted((genre1, genre2) -> random.nextInt(2) - 1)
                        .limit(3) // 상위 3개 선택
                        .collect(Collectors.toList());

                // 빌더 패턴으로 프로필 생성
                UserProfile userProfile = UserProfile.builder()
                        .nickName("TempUser" + i)
                        .gender(i % 2 == 0 ? Gender.Female : Gender.Male)
                        .profileImage("https://example.com/profile" + i + ".jpg")
                        .favoriteGenres(genres) // 장르 리스트 설정
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
