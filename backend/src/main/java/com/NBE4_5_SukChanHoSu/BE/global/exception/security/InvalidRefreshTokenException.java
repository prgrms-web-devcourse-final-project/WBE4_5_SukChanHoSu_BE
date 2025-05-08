package com.NBE4_5_SukChanHoSu.BE.global.exception.security;

public class InvalidRefreshTokenException extends SecurityException {
    public InvalidRefreshTokenException(String code, String message) {
        super(code, message);
    }
}
