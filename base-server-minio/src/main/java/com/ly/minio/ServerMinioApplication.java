package com.ly.minio;

import jakarta.annotation.Resource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.Environment;

@SpringBootApplication(scanBasePackages = {"com.ly"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.ly"})
@RefreshScope
public class ServerMinioApplication {

	@Resource
	private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(ServerMinioApplication.class, args);
	}

}
