package com.NBE4_5_SukChanHoSu.BE.global.init;

import com.NBE4_5_SukChanHoSu.BE.domain.movie.entity.Movie;
import com.NBE4_5_SukChanHoSu.BE.domain.movie.repository.MovieRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserSignUpRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Gender;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserProfileRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@ActiveProfiles("test")
public class TestInitData {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Bean
    public ApplicationRunner initData(UserProfileRepository userProfileRepository, UserService userService, UserRepository userRepository, MovieRepository movieRepository) {
        Random random = new Random();
        return args -> {
            if (userRepository.count() > 0) {
                System.out.println("⚠️ 유저가 이미 존재하여 profileInit() 스킵됨.");
                return;
            }
            // 테스트 데이터 생성
            for (int i = 1; i <= 10; i++) {
                String newEmail = "initUser" + i + "@example.com";
                redisTemplate.opsForValue().set("emailVerify:" + newEmail, "true", 5, TimeUnit.MINUTES);
                UserSignUpRequest signUpDto = UserSignUpRequest.builder()
                        .email(newEmail)
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

            // 🎬 영화 5개 삽입
            List<Movie> movies = List.of(
                    Movie.builder()
                            .movieId(20070001L)
                            .title("Inception")
                            .genresRaw("Action, Science Fiction")
                            .releaseDate("20100716")
                            .posterImage("https://image.tmdb.org/t/p/w500/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg")
                            .description("꿈속의 꿈으로 들어가는 액션 블록버스터")
                            .director("Christopher Nolan")
                            .rating("PG-13")
                            .build(),

                    Movie.builder()
                            .movieId(20070002L)
                            .title("The Matrix")
                            .genresRaw("Action, Science Fiction")
                            .releaseDate("19990331")
                            .posterImage("https://image.tmdb.org/t/p/w500/aZiK1mzNHRn7kvVxU3lK1ElGNRk.jpg")
                            .description("가상현실과 인간의 전쟁")
                            .director("Lana Wachowski, Lilly Wachowski")
                            .rating("R")
                            .build(),

                    Movie.builder()
                            .movieId(20070003L)
                            .title("La La Land")
                            .genresRaw("Romance, Music, Drama")
                            .releaseDate("20161209")
                            .posterImage("https://image.tmdb.org/t/p/w500/uDO8zWDhfWwoFdKS4fzkUJt0Rf0.jpg")
                            .description("꿈과 사랑 사이에서 갈등하는 예술가들")
                            .director("Damien Chazelle")
                            .rating("PG-13")
                            .build(),

                    Movie.builder()
                            .movieId(20070004L)
                            .title("Parasite")
                            .genresRaw("Drama, Thriller")
                            .releaseDate("20190530")
                            .posterImage("https://image.tmdb.org/t/p/w500/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg")
                            .description("기생과 공생의 이면")
                            .director("Bong Joon-ho")
                            .rating("R")
                            .build(),

                    Movie.builder()
                            .movieId(20070005L)
                            .title("Interstellar")
                            .genresRaw("Adventure, Drama, Science Fiction")
                            .releaseDate("20141107")
                            .posterImage("https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg")
                            .description("우주의 끝에서 미래를 찾다")
                            .director("Christopher Nolan")
                            .rating("PG-13")
                            .build()
            );

            movieRepository.saveAll(movies);
            System.out.println("🎬 테스트용 영화 5개 삽입 완료");
        };
    }
}
