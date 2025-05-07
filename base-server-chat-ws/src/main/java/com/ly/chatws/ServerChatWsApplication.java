package com.ly.chatws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.ly"})
@EnableFeignClients(basePackages = "com.ly")
@EnableScheduling
public class ServerChatWsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerChatWsApplication.class, args);
    }

}