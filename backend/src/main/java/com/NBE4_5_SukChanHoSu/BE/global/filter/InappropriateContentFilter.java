//package com.NBE4_5_SukChanHoSu.BE.global.filter;
//
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Set;
//
//public class InappropriateContentFilter {
//
//    // 부적절한 단어들 (실무에선 DB나 외부 관리하는 경우 많음)
//    private static final Set<String> INAPPROPRIATE_WORDS = new HashSet<>(Arrays.asList(
//            "욕설1", "욕설2", "비속어1", "비속어2",  // 예시: 실제 육두문자 등 넣기
//            "음란어1", "음란어2"
//    ));
//
//    /**
//     * 내용에 부적절한 단어 포함 여부 검사
//     * @param content 검사할 문자열
//     * @return 부적절한 단어가 포함되어 있으면 true, 아니면 false
//     */
//    public static boolean isInappropriate(String content) {
//        if (content == null || content.isEmpty()) {
//            return false;
//        }
//        String lowerContent = content.toLowerCase();
//
//        for (String word : INAPPROPRIATE_WORDS) {
//            if (lowerContent.contains(word)) {
//                return true;
//            }
//        }
//        return false;
//    }
//}
