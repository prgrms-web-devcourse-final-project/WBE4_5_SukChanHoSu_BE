package com.NBE4_5_SukChanHoSu.BE.global.exception.security;

public class BadCredentialsException extends SecurityException {
    public BadCredentialsException(String code, String message) {
        super(code, message);
    }
}
