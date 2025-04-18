package com.ly.customer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.ly"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ly")
@RefreshScope
@MapperScan(basePackages = {"com.ly.customer.mapper"})
public class WebWxMoXianApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebWxMoXianApplication.class,args);
    }
}
