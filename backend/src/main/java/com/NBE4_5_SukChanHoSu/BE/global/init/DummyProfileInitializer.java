package com.NBE4_5_SukChanHoSu.BE.global.init;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Gender;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Genre;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DummyProfileInitializer {

    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Bean
    @Order(2)
    public ApplicationRunner dummyProfileRunner() {
        return args -> {
            createDummyProfiles();
        };
    }

    public void createDummyProfiles() {
        Random random = new Random();
        List<String> genres = Arrays.asList("ACTION", "COMEDY", "DRAMA", "ROMANCE", "THRILLER",  "HORROR");
        List<String> theaters = Arrays.asList("CGV 강남", "롯데시네마 월드타워", "메가박스 코엑스", "씨네큐 신도림");
        List<String> movies = Arrays.asList("어벤져스", "기생충", "인셉션", "라라랜드", "쇼생크 탈출");
        int numberOfDummyProfiles = 10;
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());
        for (int i = 1; i <= numberOfDummyProfiles; i++) {
            UserProfile userProfile = UserProfile.builder()
                    .nickName("InitUser" + i + UUID.randomUUID().toString().substring(0, 8))
                    .gender(random.nextBoolean() ? Gender.Female : Gender.Male)
                    .profileImage("https://via.placeholder.com/150")
                    .latitude(37.5 + random.nextDouble() * 0.1)
                    .longitude(126.9 + random.nextDouble() * 0.1)
                    .birthdate(LocalDate.now().minusYears(random.nextInt(30) + 20))


                    .favoriteGenres(genres.stream().skip(random.nextInt(genres.size())).limit(random.nextInt(genres.size()) + 1).map(Genre::valueOf).toList())
                    .introduce("Init에서 생성된 더미 유저 " + i + " 입니다.")
                    .build();

            // 데이터베이스에 저장
            userProfileRepository.save(userProfile);

            // Redis에 저장 (JSON 형태로 변환하여 String으로 저장)
            try {
                String key = "initprofile:" + userProfile.getUserId();
                String value = objectMapper.writeValueAsString(userProfile);
                ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
                ops.set(key, value);
                System.out.println("Init 더미 프로필 Redis 저장 (String): 키=" + key + ", 값=" + value);
            } catch (Exception e) {
                System.err.println("JSON 변환 오류: " + e.getMessage());
            }

            System.out.println("Init 더미 프로필 생성 및 저장 완료 (" + i + "):");
            System.out.println("userId: " + userProfile.getUserId());
            System.out.println("nickName: " + userProfile.getNickName());
            System.out.println("gender: " + userProfile.getGender());
            System.out.println("favoriteGenres: " + userProfile.getFavoriteGenres());
            System.out.println("introduce: " + userProfile.getIntroduce());
            System.out.println("latitude: " + userProfile.getLatitude());
            System.out.println("longitude: " + userProfile.getLongitude());
            System.out.println("-----------------------------");
        }
    }

    private <T> List<T> getRandomSublist(List<T> list, int count) {
        Random random = new Random();
        List<T> sublist = new java.util.ArrayList<>(list);
        java.util.Collections.shuffle(sublist);
        return sublist.subList(0, Math.min(count, sublist.size()));
    }
}