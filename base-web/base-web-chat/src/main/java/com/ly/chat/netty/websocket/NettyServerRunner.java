package com.ly.chat.netty.websocket;

import com.ly.chat.netty.server.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class NettyServerRunner implements ApplicationRunner {

    @Autowired
    private NettyServer nettyServer;

    @Override
    public void run(ApplicationArguments args) {
        nettyServer.start();
    }
}