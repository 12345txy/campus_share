package com.example.campus_share.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 前端建立连接的端点（WebSocket 地址）
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // ✅ 这是前端连接地址，如 ws://localhost:8080/ws
                .setAllowedOrigins("http://localhost:8321","http://10.129.47.99:8321") // 跨域配置，开发时允许全部
                .withSockJS(); // 开启 SockJS 兼容老浏览器（前端地址要用 SockJS）
                System.out.println("✅ WebSocketConfig 被加载啦！");
    }

    // 消息代理配置：订阅路径 & 发送路径前缀
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // ✅ 用户订阅这个前缀的频道，例如 /topic/announcements
        registry.setApplicationDestinationPrefixes("/app"); // ⛔ 暂时用不到，除非你让客户端主动发消息给服务端
    }
}
