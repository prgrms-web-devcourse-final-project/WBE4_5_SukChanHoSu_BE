package com.NBE4_5_SukChanHoSu.BE.global.exception.security;

public class BlacklistedTokenException extends SecurityException {
    public BlacklistedTokenException(String code, String message) {
        super(code, message);
    }
}
