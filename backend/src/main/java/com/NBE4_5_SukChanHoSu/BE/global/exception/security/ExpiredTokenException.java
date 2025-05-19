package com.NBE4_5_SukChanHoSu.BE.global.exception.security;

public class ExpiredTokenException extends SecurityException {
    public ExpiredTokenException(String code, String message) { super(code, message); }
}
