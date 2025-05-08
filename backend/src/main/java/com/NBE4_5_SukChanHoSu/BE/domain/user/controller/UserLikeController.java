package com.NBE4_5_SukChanHoSu.BE.domain.user.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.UserLikes;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.response.LikeResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.response.MatchingResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.response.UserLikeResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.response.UserMatchingResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.UserProfileResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserLikeService;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserProfileService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.Empty;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import com.NBE4_5_SukChanHoSu.BE.global.exception.like.RelationNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Tag(name = "User API", description = "사용자 관련 API")
public class UserLikeController {

    private final UserLikeService userLikeService;
    private final UserProfileService userProfileService;

    @PostMapping("/like")
    @Operation(summary = "like 전송", description = "toUser에게 like 전송")
    // todo: 인증 구현시 파라미터에서 인증정보로 변경
    public RsData<?> likeUser(@RequestParam Long fromUserId,@RequestParam Long toUserId) {
        // 유저 탐색
        UserProfile fromUser = userProfileService.findUser(fromUserId);
        UserProfile toUser = userProfileService.findUser(toUserId);

        if(userLikeService.isSameGender(fromUser,toUser)){
            return new RsData<>("403","이성간 매칭만 허용합니다.");
        }

        // 매칭 된 사용자인지 검증
        if(userLikeService.isAlreadyMatched(fromUser,toUser)){
            return new RsData<>("403", fromUser.getNickName()+ " 와 "+toUser.getNickName()+ "님은 이미 매칭된 상태입니다.");
        }
        // 좋아요 한 사용자인지 검증
        if(userLikeService.isAlreadyLikes(fromUser,toUser)){
            return new RsData<>("403", toUser.getNickName()+ "님은 이미 like 상태입니다.");
        }

        // 좋아요
        UserLikes like = userLikeService.likeUser(fromUser,toUser);

        // 매칭 확인
        if(userLikeService.isAlreadyLiked(fromUser,toUser)){
            MatchingResponse response = userLikeService.matching(fromUser,toUser);
            return new RsData<>("200", fromUser.getNickName()+"과(와)"+toUser.getNickName()+"이 매칭 되었습니다.", response);
        }

        int radius = userProfileService.calDistance(fromUser,toUser);
        LikeResponse likeResponse = new LikeResponse(like,toUser,radius);
        return new RsData<>("200", fromUser.getNickName()+ " 가 "+toUser.getNickName()+ "님 에게 좋아요를 보냈습니다", likeResponse);
    }

    @GetMapping("/like/{userId}")
    @Operation(summary = "like 테이블 조회", description = "사용자의 like 테이블 조회")
    public RsData<UserLikeResponse> getUserLikes(@PathVariable Long userId) {
        UserProfile user = userProfileService.findUser(userId);
//        UserLikeResponse response = new UserLikeResponse();
//        response.setUserLikes(userLikeService.getUserLikes(user));
//        response.setSize(response.getUserLikes().size());
        List<UserProfileResponse> userProfileResponses = userLikeService.getUserLikes(user);
        UserLikeResponse response = new UserLikeResponse(userProfileResponses);

        if(response.getUserLikes().isEmpty()){
            return new RsData<>("404", "like 한 사용자가 없습니다.");
        }

        return new RsData<>("200",user.getNickName()+"가 좋아요한 유저 목록 반환",response);
    }

    @GetMapping("/liked/{userId}")
    @Operation(summary = "liked 테이블 조회", description = "사용자의 liked 테이블 조회")
    public RsData<UserLikeResponse> getUserLiked(@PathVariable Long userId) {
        UserProfile user = userProfileService.findUser(userId);
        List<UserProfileResponse> userProfileResponses = userLikeService.getUserLiked(user);
        UserLikeResponse response = new UserLikeResponse(userProfileResponses);
//        UserLikeResponse response = new UserLikeResponse();
//        response.setUserLikes(userLikeService.getUserLiked(user));
//        response.setSize(response.getUserLikes().size());

        if(response.getUserLikes().isEmpty()){
            return new RsData<>("404", "나를 like 하는 사용자가 없습니다.");
        }

        return new RsData<>("200",user.getNickName()+"를 좋아요한 유저 목록 반환",response);
    }

    @GetMapping("/matching/{userId}")
    @Operation(summary = "match 테이블 조회", description = "사용자의 match 테이블 조회")
    public RsData<?> getUserMatch(@PathVariable Long userId) {
        UserProfile user = userProfileService.findUser(userId);
        List<UserMatchingResponse> response = userLikeService.getUserMatches(user);
        if(response.isEmpty()){
            return new RsData<>("404", "매칭된 사용자가 없습니다.",new Empty());
        }
        return new RsData<>("200","매칭된 사용자 목록 조회("+response.size()+")",response);
    }

    @DeleteMapping("/like")
    @Operation(summary = "like/matching 취소", description = "like/matching 취소")
    // todo: 인증 구현시 파라미터에서 인증정보로 변경
    public RsData<?> cancelLikeUser(@RequestParam Long fromUserId, @RequestParam Long toUserId){
        // 유저 탐색
        UserProfile fromUser = userProfileService.findUser(fromUserId);
        UserProfile toUser = userProfileService.findUser(toUserId);

        // 매칭 된 사용자인지 검증
        if(userLikeService.isAlreadyMatched(fromUser,toUser)){
            // 매칭 취소
            userLikeService.cancelMatch(fromUser,toUser);
            return new RsData<>("200", "매칭 취소");
        }
        // 좋아요 한 사용자인지 검증
        else if(userLikeService.isAlreadyLikes(fromUser,toUser)){
            userLikeService.cancelLikes(fromUser,toUser);
            return new RsData<>("200", "like 취소");
        }
        throw new RelationNotFoundException("404", "관계 없는 사용자입니다.");
    }
}
