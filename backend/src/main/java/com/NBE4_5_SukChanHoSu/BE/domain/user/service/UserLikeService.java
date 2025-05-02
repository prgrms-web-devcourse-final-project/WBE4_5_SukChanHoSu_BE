package com.NBE4_5_SukChanHoSu.BE.domain.user.service;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.Matching;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.MatchingRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.UserLikes;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.UserLikesRepository;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.response.MatchingResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.response.UserMatchingResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.Gender;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.global.redis.config.RedisTTL;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserLikeService {

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
    private String generateMatchingKey(Long maleId, Long femaleId) {
        return "matching:" + maleId + ":" + femaleId;
    }

    // like -> 매칭
    @Transactional
    public MatchingResponse matching(UserProfile fromUser, UserProfile toUser) {
        Matching matching;
        String key;
        // fromUser가 남자인 경우
        if(isMale(fromUser)){
            matching = new Matching(fromUser, toUser);
            key = generateMatchingKey(fromUser.getUserId(),toUser.getUserId());
        }
        // fromUser가 여자인 경우(toUser가 남자인 경우)
        else {
            matching = new Matching(toUser, fromUser);
            key = generateMatchingKey(toUser.getUserId(),fromUser.getUserId());
        }
        // DB 저장
        matchingRepository.save(matching);
        // Redis 저장
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

        String pattern = "likes:" + user.getUserId() + ":*"; // likes:fromId:toId
        Set<String> keys = redisTemplate.keys(pattern);

        // 레디스에 값이 있는 경우
        if(keys != null && !keys.isEmpty()){
            System.out.println("=====레디스 확인======");
            ObjectMapper mapper = new ObjectMapper();
            for(String key : keys){
                System.out.println("###key: " + key);
                Object value = redisTemplate.opsForValue().get(key);
                if (value instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) value;  // Map(키-값 쌍)으로 캐스팅
                    try{
                        UserLikes like = mapper.convertValue(map, UserLikes.class); // Map -> UserLikes 클래스로 변환
                        likesUsers.add(like.getToUser());   // 메모리에 추가
                    }catch (IllegalArgumentException e){
                        System.err.println("JSON 역직렬화 실패: " + e.getMessage());
                        e.printStackTrace();    // todo 예외 처리
                    }
                }
            }
        }else{
            System.out.println("=====레디스에 없음=====");
            for(UserLikes like: user.getLikes()) {  // 내가 좋아요한 사용자 목록
                UserProfile likedUser = like.getToUser();   // 좋아요를 받은 사용자
                if(likedUser != null) {
                    // 내가 좋아요 한 사용자 리스트에 추가
                    likesUsers.add(likedUser);

                    // 캐싱
                    String key = "likes:" + user.getUserId() + ":" + likedUser.getUserId();
                    redisTemplate.opsForValue().set(key, like,ttl.getLikes(), TimeUnit.SECONDS);
                }
            }
        }
        return likesUsers;
    }

    // user의 liked 목록 조회
    public List<UserProfile> getUserLiked(UserProfile user) {
        List<UserProfile> likedUsers = new ArrayList<>();

        String pattern = "likes:*:" + user.getUserId(); // likes:fromId:toId
        Set<String> keys = redisTemplate.keys(pattern);

        // 레디스에 값이 있는 경우
        if(keys != null && !keys.isEmpty()){
            System.out.println("=====레디스 확인======");
            ObjectMapper mapper = new ObjectMapper();
            for(String key : keys){
                System.out.println("###key: " + key);
                Object value = redisTemplate.opsForValue().get(key);
                if (value instanceof Map) {
                    System.out.println("===== 인스턴스 찾음 ======");
                    Map<String,Object> map = (Map<String, Object>) value;
                    try{
                        UserLikes like = mapper.convertValue(map, UserLikes.class);
                        likedUsers.add(like.getFromUser());
                    }catch (IllegalArgumentException e){
                        System.err.println("JSON 역직렬화 실패: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }else{
            System.out.println("=====레디스에 없음=====");
            for(UserLikes like: user.getLikedBy()) {    // 나를 좋아요한 사용자 목록
                UserProfile likesUser = like.getFromUser(); // 좋아요를 보낸 사용자
                if(likesUser != null) {
                    // 나를 좋아요한 사용자 리스트에 추가
                    likedUsers.add(likesUser);

                    // 캐싱
                    String key = "likes:" + likesUser.getUserId() + ":" + user.getUserId();
                    redisTemplate.opsForValue().set(key, like,ttl.getLikes(), TimeUnit.SECONDS);
                }
            }
        }
        return likedUsers;
    }

    // match 목록 조회
    public List<UserMatchingResponse> getUserMatches(UserProfile user) {
        List<UserMatchingResponse> responses = new ArrayList<>();
        String pattern;
        Set<String> keys;

        // 남자 유저
        if(isMale(user)){
            pattern = "matching:" + user.getUserId()+ ":*";  // matching:maleId:femaleId
            keys = redisTemplate.keys(pattern);

            // redis 검색
            if(keys != null && !keys.isEmpty()){
                System.out.println("=====레디스 확인======");
                ObjectMapper mapper = new ObjectMapper();
                for(String key : keys){
                    Object value = redisTemplate.opsForValue().get(key);
                    if(value instanceof Map){
                        System.out.println("===== 인스턴스 찾음 ======");
                        Map<String,Object> map = (Map<String, Object>) value;
                        try{
                            Matching matching = mapper.convertValue(map, Matching.class);
                            System.out.println("추출한 유저: " + matching.getFemaleUser());
                            responses.add(new UserMatchingResponse(matching.getFemaleUser(),matching));
                        }catch (IllegalArgumentException e){
                            System.err.println("JSON 역직렬화 실패: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }else{
                // DB 조회
                System.out.println("=====레디스에 없음=====");
                List<Matching> matches = matchingRepository.findByMaleUser(user);
                for(Matching matching: matches) {
                    // 매칭된 여자 유저 리스트에 등록
                    responses.add(new UserMatchingResponse(matching.getFemaleUser(),matching));

                    // 캐싱
                    String key = "matching"+ user.getUserId() + ":" +matching.getFemaleUser().getUserId();
                    redisTemplate.opsForValue().set(key, matching,ttl.getMatching(), TimeUnit.SECONDS);
                }
            }

        }
        // 여자 유저
        else{
            pattern = "matching:*:" + user.getUserId();     // matching:maleId:femaleId
            keys = redisTemplate.keys(pattern);

            if(keys != null && !keys.isEmpty()){
                System.out.println("=====레디스 확인======");
                ObjectMapper mapper = new ObjectMapper();
                for(String key : keys){
                    Object value = redisTemplate.opsForValue().get(key);
                    if (value instanceof Map) {
                        System.out.println("===== 인스턴스 찾음 ======");
                        Map<String, Object> map = (Map<String, Object>) value;
                        try {
                            Matching matching = mapper.convertValue(map, Matching.class);
                            responses.add(new UserMatchingResponse(matching.getMaleUser(),matching));
                            System.out.println("추출한 유저: " + matching.getMaleUser());
                        }catch (IllegalArgumentException e){
                            System.err.println("JSON 역직렬화 실패: " + e.getMessage());
                            e.printStackTrace();
                        }

                        Matching matching = (Matching) value;
                        responses.add(new UserMatchingResponse(matching.getFemaleUser(),matching));
                    }
                }
            }else{
                // DB 조회
                System.out.println("=====레디스에 없음=====");
                List<Matching> matches = matchingRepository.findByFemaleUser(user);
                for(Matching matching: matches) {
                    // 매칭된 남자 유저 리스트에 등록
                    responses.add(new UserMatchingResponse(matching.getMaleUser(),matching));

                    // 캐싱
                    String redisKey = "matching:" + matching.getMaleUser().getUserId() + ":" + user.getUserId();
                    redisTemplate.opsForValue().set(redisKey, matching, ttl.getMatching(), TimeUnit.SECONDS);
                }
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
            // Redis에서 삭제
            String key = generateMatchingKey(fromUser.getUserId(), toUser.getUserId());
            redisTemplate.delete(key);
        }else{
            matchingRepository.deleteByMaleUserAndFemaleUser(toUser,fromUser);
            // Redis에서 삭제
            String key = generateMatchingKey(toUser.getUserId(), fromUser.getUserId());
            redisTemplate.delete(key);
        }
    }

    public boolean isSameGender(UserProfile fromUser, UserProfile toUser) {
        return fromUser.getGender().equals(toUser.getGender());
    }
}
