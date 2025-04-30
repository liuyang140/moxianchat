package com.ly.chat.netty.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ly.chat.netty.websocket.ChannelManager;
import com.ly.chat.netty.websocket.RedisUserSessionManager;
import com.ly.chat.service.ChatRoomService;
import com.ly.chat.service.impl.ChatMessageServiceImpl;
import com.ly.model.enums.ChatEventTypeEnum;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@ChannelHandler.Sharable // 多个连接共用此Handler
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Resource
    private ChatMessageServiceImpl chatMessageServiceImpl;

    @Resource
    private RedisUserSessionManager sessionManager;

    @Resource
    private ChatRoomService roomService;

    private static final Map<ChannelId, Long> CHANNEL_USER_MAP = new ConcurrentHashMap<>();


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        log.info("客户端连接: {}", ctx.channel().id().asLongText());
    }

    /*
    消息格式
    {
      "type": "chat",        // 类型：chat-0、bind-1、recall-2、ping-3、login -4
      "chatType": 0,         // 聊天类型：0-私聊，1-群聊
      "roomId": 10001,       // 房间ID
      "senderId": 123,       // 发送者ID
      "receiverId": 456,     //接收者id
      "content": "你好！",    // 消息正文
      "messageType": "text", // 消息类型
      "timestamp": 1688888888 // 客户端发送时间戳
    }
    * */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        JSONObject json = JSON.parseObject(msg.text());
        Integer type = json.getInteger("type");
        ChatEventTypeEnum eventType = ChatEventTypeEnum.fromValue(type);

        if (eventType == null) {
            ctx.writeAndFlush(new TextWebSocketFrame("未知消息类型:" + msg.text()));
            return;
        }

        switch (eventType) {
            case CHAT-> handleChat(ctx, json);
            case BIND -> handleBind(ctx, json);
            default -> ctx.writeAndFlush(new TextWebSocketFrame("暂不支持的事件类型:"+msg.text()));
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        Long userId = CHANNEL_USER_MAP.remove(channel.id());
        if (userId != null) {
            ChannelManager.remove(channel);
            sessionManager.offline(userId);
            log.info("用户 {} 下线", userId);
        }
    }

    private void handleBind(ChannelHandlerContext ctx, JSONObject json) {
        Long userId = json.getLong("senderId");
        Channel channel = ctx.channel();
        CHANNEL_USER_MAP.put(channel.id(), userId);
        ChannelManager.add(userId, channel);
        sessionManager.online(userId);
        log.info("用户 {} 上线", userId);

    }


    private void handleChat(ChannelHandlerContext ctx, JSONObject json) {
        // 持久化 + 转发
        chatMessageServiceImpl.saveAndForward(json);
    }


}