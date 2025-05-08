package com.NBE4_5_SukChanHoSu.BE.global.exception;

public class NullResponseException extends ServiceException {
    public NullResponseException(String code, String message) {
        super(code, message);
    }
}
