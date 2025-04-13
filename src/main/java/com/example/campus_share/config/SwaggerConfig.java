package com.example.campus_share.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.ExternalDocumentation;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI campusShareOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("校园印记 API")
                        .description("校园生活分享平台接口文档")
                        .version("v1.0.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("GitHub仓库")
                        .url("https://github.com/your-repo"));
    }
} 