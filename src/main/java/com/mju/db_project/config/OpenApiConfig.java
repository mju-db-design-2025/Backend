package com.mju.db_project.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        Server relativeServer = new Server()
                .url("/")
                .description("현재 접속한 도메인 사용");

        return new OpenAPI()
                .addServersItem(relativeServer)
                .info(new Info()
                        .title("데이터베이스설계 프로젝트 API")
                        .description("명지대학교 2025-2"));
    }
}

