package com.example.campus_share.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

@Configuration
@Order(1)  // 添加顺序注解，确保优先加载
public class DdlConfig {
    
    @Bean
    @Primary
    public ApplicationRunner ddlApplicationRunner() {
        return args -> {
            // 空实现，仅提供Bean
        };
    }
}
