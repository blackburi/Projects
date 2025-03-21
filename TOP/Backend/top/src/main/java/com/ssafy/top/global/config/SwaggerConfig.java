package com.ssafy.top.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.url}")
    private String url;

    @Bean
    public OpenAPI openAPI() {

        Server server = new Server();
        server.setUrl(url);

        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .servers(List.of(server));
    }

    private Info apiInfo() {
        return new Info()
                .title("Timo 프로젝트 API")
                .description("집중 시간을 확인하는 기능을 제공합니다.")
                .version("1.0.0");
    }
}
