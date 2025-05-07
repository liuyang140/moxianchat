package com.ly.chatws.handler;

import com.ly.common.constant.SystemConstant;
import com.ly.common.util.JwtUtils;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ChannelHandler.Sharable // 多个连接共用此Handler
public class AuthHandshakeHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        String token = request.headers().get(SystemConstant.TOKEN);
        if (token != null && !token.isEmpty()) {
            try {
                Long userId = JwtUtils.getUserIdFromToken(token);
                ctx.channel().attr(AttributeKey.valueOf(SystemConstant.TOKEN)).set(token);
                ctx.channel().attr(AttributeKey.valueOf(SystemConstant.USER_ID)).set(userId);

                log.info("Token 与 UserId 设置成功: userId={}, token={}", userId, token);

                ctx.fireChannelRead(request.retain());
            } catch (Exception e) {
                log.info("Token 解析失败，token={}", token, e);
                sendUnauthorizedResponse(ctx);
            }
        } else {
            log.info("请求中未携带 Token");
            sendUnauthorizedResponse(ctx);
        }

    }

    private void sendUnauthorizedResponse(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED
        );
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}