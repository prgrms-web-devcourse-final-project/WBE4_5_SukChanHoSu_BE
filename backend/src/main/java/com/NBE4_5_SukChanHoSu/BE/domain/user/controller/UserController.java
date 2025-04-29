package com.NBE4_5_SukChanHoSu.BE.domain.user.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.UserLikeResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.MatchingResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.UserMatchingResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.likes.dto.LikeResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserProfile;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.Empty;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Tag(name = "User API", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;

    @PostMapping("/like")
    @Operation(summary = "like 전송", description = "toUser에게 like 전송")
    // todo: 인증 구현시 파라미터에서 인증정보로 변경
    public RsData<?> likeUser(@RequestParam Long fromUserId,@RequestParam Long toUserId) {
        // 유저 탐색
        UserProfile fromUser = userService.findUser(fromUserId);
        UserProfile toUser = userService.findUser(toUserId);

        // 매칭 된 사용자인지 검증
        if(userService.isAlreadyMatched(fromUser,toUser)){
            return new RsData<>("403", fromUser.getNickName()+ " 와 "+toUser.getNickName()+ "님은 이미 매칭된 상태입니다.");
        }
        // 좋아요 한 사용자인지 검증
        if(userService.isAlreadyLikes(fromUser,toUser)){
            return new RsData<>("403", toUser.getNickName()+ "님은 이미 like 상태입니다.");
        }

        // 좋아요
        userService.likeUser(fromUser,toUser);

        // 매칭 확인
        if(userService.isAlreadyLiked(fromUser,toUser)){
            MatchingResponse response = userService.matching(fromUser,toUser);
            return new RsData<>("200", fromUser.getNickName()+"과(와)"+toUser.getNickName()+"이 매칭 되었습니다.", response);
        }

        LikeResponse likeResponse = new LikeResponse();
        likeResponse.setFromUser(fromUser);
        likeResponse.setToUser(toUser);
        return new RsData<>("200", fromUser.getNickName()+ " 가 "+toUser.getNickName()+ "님 에게 좋아요를 보냈습니다", likeResponse);
    }

    @GetMapping("/like/{userId}")
    @Operation(summary = "like 테이블 조회", description = "사용자의 like 테이블 조회")
    public RsData<UserLikeResponse> getUserLikes(@PathVariable Long userId) {
        UserProfile user = userService.findUser(userId);
        UserLikeResponse response = new UserLikeResponse();
        response.setUserLikes(userService.getUserLikes(user));
        response.setSize(response.getUserLikes().size());

        if(response.getUserLikes().isEmpty()){
            return new RsData<>("404", "like 한 사용자가 없습니다.");
        }

        return new RsData<>("200",user.getNickName()+"가 좋아요한 유저 목록 반환",response);
    }

    @GetMapping("/liked/{userId}")
    @Operation(summary = "liked 테이블 조회", description = "사용자의 liked 테이블 조회")
    public RsData<UserLikeResponse> getUserLiked(@PathVariable Long userId) {
        UserProfile user = userService.findUser(userId);
        UserLikeResponse response = new UserLikeResponse();
        response.setUserLikes(userService.getUserLiked(user));
        response.setSize(response.getUserLikes().size());

        if(response.getUserLikes().isEmpty()){
            return new RsData<>("404", "나를 like 하는 사용자가 없습니다.");
        }

        return new RsData<>("200",user.getNickName()+"를 좋아요한 유저 목록 반환",response);
    }

    @GetMapping("/matching/{userId}")
    @Operation(summary = "match 테이블 조회", description = "사용자의 match 테이블 조회")
    public RsData<?> getUserMatch(@PathVariable Long userId) {
        UserProfile user = userService.findUser(userId);
        List<UserMatchingResponse> response = userService.getUserMatches(user);
        if(response.isEmpty()){
            return new RsData<>("404", "매칭된 사용자가 없습니다.",new Empty());
        }
        return new RsData<>("200","매칭된 사용자 목록 조회("+response.size()+")",response);
    }

}
