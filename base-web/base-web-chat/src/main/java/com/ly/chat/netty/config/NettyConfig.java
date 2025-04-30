package com.ly.chat.netty.config;

import com.ly.chat.netty.handler.WebSocketHandler;
import com.ly.chat.netty.websocket.WebSocketInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyConfig {

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Bean
    public WebSocketInitializer webSocketInitializer() {
        return new WebSocketInitializer(webSocketHandler);
    }
}