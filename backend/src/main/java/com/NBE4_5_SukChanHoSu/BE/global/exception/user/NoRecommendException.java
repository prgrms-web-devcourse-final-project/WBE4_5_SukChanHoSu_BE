package com.NBE4_5_SukChanHoSu.BE.global.exception.user;

import com.NBE4_5_SukChanHoSu.BE.global.exception.security.SecurityException;

public class NoRecommendException extends SecurityException {
    public NoRecommendException(String code, String message) {
        super(code, message);
    }
}
