package com.example.backend.config;

import com.example.backend.controller.NotificationHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new NotificationHandler(), "/ws/notifications")
                .setAllowedOrigins("*");
    }
}

//import com.example.backend.controller.NotificationHandler;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//
//@Configuration
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketConfigurer {
//
//    private final NotificationHandler notificationHandler;
//
//    public WebSocketConfig(NotificationHandler notificationHandler) {
//        this.notificationHandler = notificationHandler;
//    }
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(notificationHandler, "/notifications")
//                .setAllowedOrigins("*"); // Cho phép mọi nguồn gốc truy cập
//    }
//}


