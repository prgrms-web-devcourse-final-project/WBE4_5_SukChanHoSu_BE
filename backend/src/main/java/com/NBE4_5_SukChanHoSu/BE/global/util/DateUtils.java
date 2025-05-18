package com.NBE4_5_SukChanHoSu.BE.global.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    // 1주일 전 날짜를 yyyyMMdd 으로 반환
    public static String getOneWeekAgoDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7); // 1주일 전으로 설정
        Date oneWeekAgo = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(oneWeekAgo);
    }

    public static String getTimeAgo(Date time) {
        Date now = new Date();
        long diffInMillis = now.getTime() - time.getTime(); // 시간 차이 (밀리초)

        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis); // 분 단위 차이
        long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis);     // 시간 단위 차이
        long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);       // 일 단위 차이

        if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else if (days == 1) {
            return "어제";
        } else if (days < 7) {
            return days + "일 전";
        } else {
            long weeks = days / 7; // 주 단위 차이
            return weeks + "주일 전";
        }
    }

    public static Date parseDate(String dateString) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        return format.parse(dateString);
    }
}