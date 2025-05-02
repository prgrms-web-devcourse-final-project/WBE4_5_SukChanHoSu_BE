package com.NBE4_5_SukChanHoSu.BE.domain.user.service;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.Matching;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.MatchingRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.UserLikes;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.UserLikesRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.response.MatchingResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.response.UserMatchingResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Gender;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.repository.UserProfileRepository;
import com.NBE4_5_SukChanHoSu.BE.global.redis.config.RedisTTL;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserLikeService {

    private final UserProfileRepository userProfileRepository;
    private final UserLikesRepository userLikesRepository;
    private final MatchingRepository matchingRepository;
    private final EntityManager entityManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTTL ttl;


    @Transactional
    public UserLikes likeUser(UserProfile fromUser, UserProfile toUser) {
        // 좋아요 관계 생성
        UserLikes like = new UserLikes(fromUser, toUser);
        userLikesRepository.save(like);

        // Redis에 저장
        String key = "likes:" + fromUser.getUserId() + ":" + toUser.getUserId();
        redisTemplate.opsForValue().set(key, like,ttl.getLikes(), TimeUnit.SECONDS); // TTL 설정

        return like;
    }

    // to -> from 관계도 존재하는지 확인
    public boolean isAlreadyLiked(UserProfile fromUser, UserProfile toUser) {
        return userLikesRepository.existsByFromUserAndToUser(toUser,fromUser);
    }

    // 이미 좋아요한 상황인지 검증
    public boolean isAlreadyLikes(UserProfile fromUser, UserProfile toUser) {
        return userLikesRepository.existsByFromUserAndToUser(fromUser,toUser);
    }

    // 매칭 키 생성 메서드
    private String generateMatchingKey(Long userId1, Long userId2) {
        Long smallerId = Math.min(userId1, userId2);
        Long largerId = Math.max(userId1, userId2);
        return "matching:" + smallerId + ":" + largerId;
    }

    // like -> 매칭
    @Transactional
    public MatchingResponse matching(UserProfile fromUser, UserProfile toUser) {
        Matching matching;

        // fromUser가 남자인 경우
        if(isMale(fromUser)){
            matching = new Matching(fromUser, toUser);
        }
        // fromUser가 여자인 경우(toUser가 남자인 경우)
        else {
            matching = new Matching(toUser, fromUser);
        }

        matchingRepository.save(matching);

        // Redis에 저장
        String key = generateMatchingKey(fromUser.getUserId(), toUser.getUserId()); // 정렬해서 키에 저장
        redisTemplate.opsForValue().set(key, matching,ttl.getMatching(), TimeUnit.SECONDS); // TTL 설정

        // 좋아요 관계 삭제
        cancelLikes(fromUser, toUser);
        cancelLikes(toUser, fromUser);

        // 응답 생성
        MatchingResponse matchingResponse = new MatchingResponse();
        matchingResponse.setMatching(matching);
        return matchingResponse;
    }

    // like 목록 조회
    public List<UserProfile> getUserLikes(UserProfile user) {
        List<UserProfile> likesUsers = new ArrayList<>();

        for(UserLikes like: user.getLikes()) {  // 내가 좋아요한 사용자 목록
            UserProfile likedUser = like.getToUser();   // 좋아요를 받은 사용자
            if(likedUser != null) {
                // 내가 좋아요 한 사용자 리스트에 추가
                likesUsers.add(likedUser);
            }
        }
        return likesUsers;
    }

    // user의 liked 목록 조회
    public List<UserProfile> getUserLiked(UserProfile user) {
        List<UserProfile> likedUsers = new ArrayList<>();

        for(UserLikes like: user.getLikedBy()) {    // 나를 좋아요한 사용자 목록
            UserProfile likesUser = like.getFromUser(); // 좋아요를 보낸 사용자
            if(likesUser != null) {
                // 나를 좋아요한 사용자 리스트에 추가
                likedUsers.add(likesUser);
            }
        }
        return likedUsers;
    }

    // match 목록 조회
    public List<UserMatchingResponse> getUserMatches(UserProfile user) {

        List<UserMatchingResponse> responses = new ArrayList<>();
        // 남자 유저
        if(isMale(user)){
            List<Matching> matches = matchingRepository.findByMaleUser(user);
            for(Matching matching: matches) {
                // 매칭된 여자 유저 리스트에 등록
                responses.add(new UserMatchingResponse(matching.getFemaleUser(),matching));
            }
        }
        // 여자 유저
        else {
            List<Matching> matches = matchingRepository.findByFemaleUser(user);
            for(Matching matching: matches) {
                // 매칭된 남자 유저 리스트에 등록
                responses.add(new UserMatchingResponse(matching.getMaleUser(),matching));
            }
        }
        return responses;
    }

    // 남자인지 검증 (남자:true,여자:false)
    public boolean isMale(UserProfile user) {
        return user.getGender().equals(Gender.Male);
    }

    // 매칭테이블에 이미 있는지 검증
    public boolean isAlreadyMatched(UserProfile fromUser, UserProfile toUser) {
        if(isMale(fromUser)){
            return matchingRepository.existsByMaleUserAndFemaleUser(fromUser,toUser);
        }else{
            return matchingRepository.existsByMaleUserAndFemaleUser(toUser,fromUser);
        }
    }

    @Transactional
    public void cancelLikes(UserProfile fromUser, UserProfile toUser) {
        userLikesRepository.deleteByFromUserAndToUser(fromUser,toUser);
        /*
         DB 반영
         삭제 연산 수행 후, 남아있는 엔티티 정보를 제거
         detach 상태로 변경 -> DB에서 새로운 데이터를 가져오도록 보장
         */
        entityManager.flush();
        entityManager.clear();

        // Redis에서 삭제
        String key = "likes:" + fromUser.getUserId() + ":" + toUser.getUserId();
        redisTemplate.delete(key);
    }

    @Transactional
    public void cancelMatch(UserProfile fromUser, UserProfile toUser) {
        if(isMale(fromUser)){
            matchingRepository.deleteByMaleUserAndFemaleUser(fromUser,toUser);
        }else{
            matchingRepository.deleteByMaleUserAndFemaleUser(toUser,fromUser);
        }

        // Redis에서 삭제
        String key = generateMatchingKey(fromUser.getUserId(), toUser.getUserId());
        redisTemplate.delete(key);
    }

    public boolean isSameGender(UserProfile fromUser, UserProfile toUser) {
        return fromUser.getGender().equals(toUser.getGender());
    }
}
