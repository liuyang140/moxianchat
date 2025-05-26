package com.ly.common.handler;

import com.ly.common.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class StartupSuccessPrinterHandler {

    @Autowired
    private Environment environment;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        String port = environment.getProperty("server.port", "8080");
        String appName = environment.getProperty("spring.application.name", "ServerMinioApplication");
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        String hostAddress = "localhost";
        try {
            hostAddress = IpUtil.getLocalIp();
        } catch (Exception e) {
            // 保持默认 localhost
        }

        System.out.println("\n" +
                "------------------------------------------------------------\n" +
                "  🚀 " + appName + " 启动成功！\n" +
                "  🌐 服务地址:       http://"+hostAddress+":" + port + "\n" +
                "  🕒 启动时间:       " + time + "\n" +
                "------------------------------------------------------------\n"
        );
    }
}