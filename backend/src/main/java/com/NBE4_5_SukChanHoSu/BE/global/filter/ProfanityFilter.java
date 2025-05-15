//package com.NBE4_5_SukChanHoSu.BE.global.filter;
//
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.regex.Pattern;
//
//@Component
//public class ProfanityFilter {
//
//    // 실무에 자주 쓰이는 욕설 키워드 샘플 (필요에 따라 확장, DB 연동 가능)
//    private final List<String> profanityList = Arrays.asList(
//            "씨발", "좆", "개새끼", "병신", "ㅅㅂ", "ㅈ같", "꺼져", "미친", "염병"
//    );
//
//    private final List<Pattern> profanityPatterns;
//
//    public ProfanityFilter() {
//        profanityPatterns = profanityList.stream()
//                .map(word -> Pattern.compile(Pattern.quote(word), Pattern.CASE_INSENSITIVE))
//                .toList();
//    }
//
//    /**
//     * 입력 문자열에 욕설 포함 여부 체크
//     * @param content 검사할 문자열
//     * @return 욕설 포함 시 true
//     */
//    public boolean containsProfanity(String content) {
//        if (content == null || content.isEmpty()) return false;
//
//        for (Pattern pattern : profanityPatterns) {
//            if (pattern.matcher(content).find()) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 욕설 마스킹 처리 (ex. "씨발" -> "ㅇㅇ")
//     * @param content 원본 문자열
//     * @return 마스킹 처리된 문자열
//     */
//    public String maskProfanity(String content) {
//        if (content == null || content.isEmpty()) return content;
//
//        String masked = content;
//        for (Pattern pattern : profanityPatterns) {
//            masked = pattern.matcher(masked).replaceAll("ㅇㅇ");
//        }
//        return masked;
//    }
//}
