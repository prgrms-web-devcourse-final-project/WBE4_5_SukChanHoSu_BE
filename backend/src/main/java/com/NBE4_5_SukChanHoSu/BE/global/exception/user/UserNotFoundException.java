package com.NBE4_5_SukChanHoSu.BE.global.exception.user;

import com.NBE4_5_SukChanHoSu.BE.global.exception.security.SecurityException;

public class UserNotFoundException extends SecurityException {
    public UserNotFoundException(String code, String message) {
        super(code, message);
    }
}
