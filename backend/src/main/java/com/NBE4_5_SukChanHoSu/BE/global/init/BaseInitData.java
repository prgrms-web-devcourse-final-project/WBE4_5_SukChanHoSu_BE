package com.NBE4_5_SukChanHoSu.BE.global.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.NBE4_5_SukChanHoSu.BE.domain.user.Genre;
import com.NBE4_5_SukChanHoSu.BE.domain.user.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Configuration
@Profile("!monitor")
@RequiredArgsConstructor
public class BaseInitData {

	@Autowired
	@Lazy
	private BaseInitData self;
	@Autowired
	private UserProfileRepository userProfileRepository;

	@Bean
	@Order(1)
	public ApplicationRunner applicationRunner1() {
		return args -> {
			self.profileInit();
		};
	}


	@Transactional
	public void profileInit() {
		for (int i = 1; i <= 10; i++) {
			// 임시 유저 프로필 생성
			UserProfile userProfile = new UserProfile();
			userProfile.setNickName("TempUser" + i);
			userProfile.setGender(i % 2 == 0 ? "Female" : "Male");
			userProfile.setProfileImage("https://example.com/profile" + i + ".jpg");

			// Enum으로 장르를 설정 (임의로 Action, Comedy, Drama 선택)
			List<Genre> genres = List.of(Genre.ACTION, Genre.COMEDY, Genre.DRAMA);
			userProfile.setFavouriteGenres(genres); // 장르 리스트 설정

			userProfile.setIntroduce("안녕하세요! 임시 유저 " + i + "입니다.");
			userProfile.setLatitude(37.5665 + (i * 0.01)); // 임의의 위도 값
			userProfile.setLongitude(126.9780 + (i * 0.01)); // 임의의 경도 값

			// 데이터베이스에 저장
			userProfileRepository.save(userProfile);

			// 로그 출력
			System.out.println("임시 유저 프로필 데이터 생성 완료 (" + i + "):");
			System.out.println("userId: " + userProfile.getUserId());
			System.out.println("nickName: " + userProfile.getNickName());
			System.out.println("gender: " + userProfile.getGender());
			System.out.println("profileImage: " + userProfile.getProfileImage());
			System.out.println("favoriteGenres: " + userProfile.getFavouriteGenres());
			System.out.println("introduce: " + userProfile.getIntroduce());
			System.out.println("latitude: " + userProfile.getLatitude());
			System.out.println("longitude: " + userProfile.getLongitude());
			System.out.println("-----------------------------");
		}
	}

}
