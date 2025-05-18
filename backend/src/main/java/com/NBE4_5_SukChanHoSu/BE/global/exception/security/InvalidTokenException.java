package com.NBE4_5_SukChanHoSu.BE.global.exception.security;

public class InvalidTokenException extends SecurityException {
    public InvalidTokenException(String code, String message) {
        super(code, message);
    }
}
