package com.NBE4_5_SukChanHoSu.BE.domain.user.dto.response;

import com.NBE4_5_SukChanHoSu.BE.domain.user.entity.User;
import lombok.Data;

@Data
public class UserResponse {
    private String email;
    private String password;
    private String role;

    public UserResponse(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole().name();
    }
}
