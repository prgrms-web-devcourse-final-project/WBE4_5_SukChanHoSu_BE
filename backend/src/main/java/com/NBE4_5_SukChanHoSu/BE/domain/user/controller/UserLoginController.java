package com.NBE4_5_SukChanHoSu.BE.domain.user.controller;

import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response.UserResponse;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserLoginRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.dto.request.UserSignUpRequest;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.UserSuccessCode;
import com.NBE4_5_SukChanHoSu.BE.domain.user.service.UserService;
import com.NBE4_5_SukChanHoSu.BE.global.dto.RsData;
import com.NBE4_5_SukChanHoSu.BE.global.jwt.JwtTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserLoginController {
    private final UserService userService;

    @PostMapping("/join")
    public RsData<UserResponse> join(@RequestBody UserSignUpRequest requestDto) {
        User user = userService.join(requestDto);

        return new RsData<>(
                UserSuccessCode.JOIN_SUCCESS.getCode(),
                UserSuccessCode.JOIN_SUCCESS.getMessage(),
                new UserResponse(user)
        );
    }

    @PostMapping("/login")
    public RsData<JwtTokenDto> login(@RequestBody UserLoginRequest requestDto) {
        return new RsData<>(
                UserSuccessCode.LOGIN_SUCCESS.getCode(),
                UserSuccessCode.LOGIN_SUCCESS.getMessage(),
                userService.login(requestDto)
        );
    }
}
