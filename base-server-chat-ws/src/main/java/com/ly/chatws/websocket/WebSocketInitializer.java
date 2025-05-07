package com.ly.chatws.websocket;

import com.ly.chatws.handler.AuthHandshakeHandler;
import com.ly.chatws.handler.WebSocketHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class WebSocketInitializer extends ChannelInitializer<SocketChannel> {

    private final WebSocketHandler webSocketHandler;

    private final AuthHandshakeHandler authHandshakeHandler;

    public WebSocketInitializer(WebSocketHandler webSocketHandler,  AuthHandshakeHandler authHandshakeHandler) {
        this.webSocketHandler = webSocketHandler;
        this.authHandshakeHandler = authHandshakeHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(authHandshakeHandler);
        pipeline.addLast(new WebSocketServerProtocolHandler("/chat"));
        pipeline.addLast(webSocketHandler); // 注意不能new
    }
}