package com.ly.chatws.websocket;

import com.ly.chatws.server.NettyServer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
@Slf4j
public class NettyServerRunner implements ApplicationRunner {

    @Autowired
    private NettyServer nettyServer;

    @Resource(name = "customThreadPool")
    private Executor customThreadPool;


    @Override
    public void run(ApplicationArguments args) {
        customThreadPool.execute(() -> {
            try {
                nettyServer.start();
                log.info("======== Netty聊天服务器 启动成功 ========");
            } catch (Exception e) {
                log.error("Netty 启动失败", e);
            }
        });
    }
}
