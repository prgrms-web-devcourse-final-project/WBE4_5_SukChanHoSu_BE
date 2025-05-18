package com.NBE4_5_SukChanHoSu.BE.domain.email.template;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplateBuilder {
    private static final String EMAIL_TEMPLATE = """
            <div style="max-width: 600px; margin: 0 auto; font-family: 'Arial', sans-serif; background-color: #f9f9f9; padding: 30px; border-radius: 10px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);">
                <h2 style="text-align: center; color: #333;">이메일 인증 요청</h2>
                <p style="font-size: 16px; color: #555;">안녕하세요,</p>
                <p style="font-size: 16px; color: #555;">요청하신 인증 코드는 아래와 같습니다:</p>
                <div style="margin: 20px 0; text-align: center;">
                    <span style="display: inline-block; font-size: 28px; font-weight: bold; background-color: #e0f7fa; color: #00796b; padding: 15px 30px; border-radius: 8px;">
                        %s
                    </span>
                </div>
                <p style="font-size: 14px; color: #999;">본 코드는 5분간 유효합니다.</p>
                <p style="font-size: 16px; color: #555;">감사합니다.</p>
                <hr style="margin-top: 30px;">
                <p style="font-size: 12px; color: #aaa; text-align: center;">본 이메일은 발신 전용입니다. 문의는 사이트를 통해 부탁드립니다.</p>
            </div>
            """;

    public String buildAuthEmail(String authCode) {
        return EMAIL_TEMPLATE.formatted(authCode);
    }
}
