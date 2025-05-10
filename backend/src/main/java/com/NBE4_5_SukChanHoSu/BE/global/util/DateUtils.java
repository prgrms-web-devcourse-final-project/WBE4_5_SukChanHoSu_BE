package com.NBE4_5_SukChanHoSu.BE.global.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    // 1주일 전 날짜를 yyyyMMdd 으로 반환
    public static String getOneWeekAgoDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7); // 1주일 전으로 설정
        Date oneWeekAgo = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(oneWeekAgo);
    }
}