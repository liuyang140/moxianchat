package com.ly.chat.netty.server;

import com.ly.chat.netty.handler.WebSocketHandler;
import com.ly.chat.netty.websocket.WebSocketInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
@Slf4j
public class NettyServer {

    @Value("${netty.port}")
    private int port;

    @Resource(name = "customThreadPool")
    private Executor customThreadPool;

    @Autowired
    private WebSocketHandler webSocketHandler;

    public void start() {
        customThreadPool.execute(() -> {
            EventLoopGroup boss = new NioEventLoopGroup();
            EventLoopGroup worker = new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(boss, worker)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new WebSocketInitializer(webSocketHandler));

                ChannelFuture future = bootstrap.bind(port).sync();
                log.info("聊天服务器启动 ------- 端口号：" + port);
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("Netty 启动异常", e);
            } finally {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            }
        });
    }
}