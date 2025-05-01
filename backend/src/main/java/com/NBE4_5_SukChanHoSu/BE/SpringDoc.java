package com.NBE4_5_SukChanHoSu.BE;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDoc {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new io.swagger.v3.oas.models.security.SecurityScheme()
                                .type(SecurityScheme.Type.HTTP) // HTTP 타입으로 설정
                                .scheme("bearer") // Bearer 방식 적용
                                .bearerFormat("JWT") // JWT 형식 지정
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization"))
                        .addSchemas("Multipart", new Schema().type("string").format("binary"))) // Multipart 파일 업로드를 위한 스키마 추가)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .info(apiInfo());
    }

    private Info apiInfo() {

        return new Info()
                .title("API Test")
                .description("api 테스트입니다.")
                .version("1.0.0");
    }
}
