package com.ly.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.ly"})
@EnableDiscoveryClient
@EnableFeignClients
@RefreshScope
public class ServerGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerGatewayApplication.class, args);
	}

}
