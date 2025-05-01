package com.NBE4_5_SukChanHoSu.BE.global.exception.like;

import com.NBE4_5_SukChanHoSu.BE.global.exception.security.SecurityException;

public class RelationNotFoundException extends SecurityException {
    public RelationNotFoundException(String code, String message) {
        super(code, message);
    }
}
