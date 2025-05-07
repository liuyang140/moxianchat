package com.ly.chatws.handler;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.ly.chatws.util.ClientUtils;
import com.ly.chatws.websocket.ChannelManager;
import com.ly.chatws.websocket.RedisUserSessionManager;
import com.ly.common.constant.SystemConstant;
import com.ly.common.result.ResultCodeEnum;
import com.ly.common.result.WsResult;
import com.ly.common.util.AuthContextHolder;
import com.ly.model.dto.chat.ChatMessageDTO;
import com.ly.model.dto.chat.UpdateMessageDTO;
import com.ly.model.enums.ChatEventTypeEnum;
import com.ly.model.enums.ChatTypeEnum;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@ChannelHandler.Sharable // 多个连接共用此Handler
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

 /*   @Resource
    private ChatMessageService chatMessageService;*/

    @Resource
    private RedisUserSessionManager sessionManager;

    private static final Map<ChannelId, Long> CHANNEL_USER_MAP = new ConcurrentHashMap<>();
    @Autowired
    private ClientUtils clientUtils;


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        log.info("客户端连接: {}", ctx.channel().id().asLongText());
    }

    /*
    消息格式
    {
      "type": 0,        // 类型：chat-0、bind-1、recall-2、ping-3、login -4
      "chatType": 0,         // 聊天类型：0-私聊，1-群聊
      "roomId": 10001,       // 房间ID
      "senderId": 123,       // 发送者ID
      "receiverId": 456,     //接收者id
      "content": "你好！",    // 消息正文
      "messageType": 0, // 消息类型 0-文本，1-图片，2-文件
      "timestamp": 1688888888 // 客户端发送时间戳
      "messageId": 123456789 // 消息ID
      "messageStatus": 0 // 消息状态 0-正常，1-撤回
    }
    * */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {

        log.info("收到消息: {}", msg.text());
        JSONObject json = JSON.parseObject(msg.text());
        Integer type = json.getInteger("type");
        ChatEventTypeEnum eventType = ChatEventTypeEnum.fromValue(type);

        if (eventType == null) {
            sendFail(ctx,  "未知消息类型");
            return;
        }

        switch (eventType) {
            case CHAT-> handleChat(ctx, json);
            case BIND -> handleBind(ctx, json);
            case RECALL -> handleRecall(ctx, json);
            case PING -> handlePing(ctx, json);
            case JOIN_GROUP_ROOM -> handleJoinRoom(ctx, json);
            case LEAVE_GROUP_ROOM -> handleLeaveRoom(ctx, json);
            case ONLINE_USERS -> handleOnlineUsers(ctx, json);
            default -> sendFail(ctx,  "暂不支持的事件类型");
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        /*  Channel channel = ctx.channel();
        Long userId = CHANNEL_USER_MAP.remove(channel.id());
        if (userId != null) {
            ChannelManager.remove(channel);
            sessionManager.offline(userId);// 只清除连接状态，保留房间关系，支持断线重连
            log.info("用户 {} 下线", userId);
        }*/
        safeRemove(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        sendFail(ctx,"服务器异常");
        log.error("WebSocket异常: {}", cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        safeRemove(ctx); // 复用原下线逻辑
        super.channelInactive(ctx);
    }

    /*
    * {
          "type": 4,
          "roomId": 123,
          "userId": 1001
        }
    * */
    private void handleJoinRoom(ChannelHandlerContext ctx, JSONObject json) {
        Long roomId = json.getLong("roomId");
        Long userId = json.getLong("userId");

        ChannelManager.joinRoom(roomId, userId);
        sessionManager.saveRoomUser(roomId, userId);

        // 回复客户端
        sendSuccess(ctx,ChatEventTypeEnum.JOIN_GROUP_ROOM,null,"加入群聊成功");
    }

    private void handleLeaveRoom(ChannelHandlerContext ctx, JSONObject json) {
        Long roomId = json.getLong("roomId");
        Long userId = json.getLong("userId");

        ChannelManager.leaveRoom(roomId, userId);

        // 回复客户端
        sendSuccess(ctx,ChatEventTypeEnum.LEAVE_GROUP_ROOM,null,"已退出群聊");
    }

    private void handleOnlineUsers(ChannelHandlerContext ctx, JSONObject json) {
        Long roomId = json.getLong("roomId");

        Set<Long> userIds = ChannelManager.getRoomUsers(roomId);

        sendSuccess(ctx,ChatEventTypeEnum.ONLINE_USERS, userIds, "当前房间用户列表查询成功");

    }


    /*
    * {
          "type": 4,
          "customerId": 1234,
          "timestamp": 1714976542000
       }
       10-20秒发送一次心跳包
    * */
    private void handlePing(ChannelHandlerContext ctx, JSONObject json) {
        Long customerId = json.getLong("customerId");
        ChannelManager.refreshLastActiveTime(customerId); // 更新活跃时间
        sessionManager.refreshAllTTL(customerId); //刷新redis所有关联ttl
        //TODO 持久层更新用户活跃时间，feign远程调用 ... 暂不实现

        sendSuccess(ctx,ChatEventTypeEnum.PING,null, "pong");
    }

    /*
      {
          "type": 2,
          "messageId": 12345,
          "roomId": 1001,
          "senderId": 2001
      }
   */
    private void handleRecall(ChannelHandlerContext ctx, JSONObject json) {
//        chatMessageService.recallMessage(json);

        ChatMessageDTO chatMessageDTO = json.toJavaObject(ChatMessageDTO.class);
        List<ChatMessageDTO> reCallMessages = clientUtils.reCallMessages(new UpdateMessageDTO().setChatMessageDTOList(CollUtil.newArrayList(chatMessageDTO)), ctx);
        reCallMessages.forEach(message -> {
            this.forwardMessage(message,ctx);
        });
    }

    /*
      {
          "type": 1,
          "senderId": 2001
      }
    */
    private void handleBind(ChannelHandlerContext ctx, JSONObject json) {
        Long userId = json.getLong("senderId");
        Channel newChannel = ctx.channel();
        ChannelId newChannelId = newChannel.id();


        // 检查是否已有连接（是否重连）
        Channel oldChannel = ChannelManager.get(userId);
        boolean isReconnect = (oldChannel != null && oldChannel.isActive() && oldChannel != newChannel);

        if (isReconnect) {
            // 主动踢下线旧连接
            log.info("用户 {} 重连，踢出旧 channel {}", userId, oldChannel.id().asLongText());

            // 发送被踢通知
            sendWsResult(oldChannel,WsResult.build(ChatEventTypeEnum.KICK.getValue(),null, ResultCodeEnum.PERMISSION.getCode(), "您已在其他地方登录，请重新登录"));

            // 清理旧 channel 本地与 Redis 状态
            CHANNEL_USER_MAP.remove(oldChannel.id(), userId);
            ChannelManager.remove(oldChannel);
            sessionManager.offline(userId);

            // 关闭旧连接
            oldChannel.close();
        }

        // 本地缓存映射
        CHANNEL_USER_MAP.put(newChannel.id(), userId);
        ChannelManager.add(userId, newChannel);

        // 设置用户在线状态
        sessionManager.online(userId);

        // 绑定 channelId 到 Redis
        sessionManager.bindChannel(userId, newChannelId.asLongText());

        // 恢复房间关系
        Set<String> roomIds = sessionManager.getUserRooms(userId);
        if (roomIds != null) {
            for (String roomId : roomIds) {
                ChannelManager.joinRoom(Long.valueOf(roomId), userId);
            }
            log.info("恢复用户 {} 的房间列表: {}", userId, roomIds);
        }

        log.info("用户 {} {}，绑定 channel {}", userId, isReconnect ? "重连成功" : "上线", newChannelId.asLongText());

        // 向客户端通知重连或首次登录成功，及房间列表
        sendSuccess(ctx, ChatEventTypeEnum.BIND, Map.of(
                "reconnect", isReconnect,
                "rooms", roomIds != null ? roomIds : Set.of()
        ), isReconnect ? "重连成功" : "绑定成功");
    }

    /*
    {
      "type": 0,
      "chatType": 0,
      "roomId": 10001,
      "senderId": 2,
      "receiverId": 1,
      "content": "big蛋！",
      "messageType": 0,
      "timestamp": 1688888888
    }
    * */
    private void handleChat(ChannelHandlerContext ctx, JSONObject json) {
        if (sessionManager.getChannelId(json.getLong("senderId")) == null) {
            sendFail(ctx, "请先绑定用户");
            return;
        }
        ChatMessageDTO dto = json.toJavaObject(ChatMessageDTO.class);
        if(clientUtils.saveMessages(new UpdateMessageDTO().setChatMessageDTOList(CollUtil.toList(dto)),ctx)){
            this.forwardMessage(dto, ctx);
        }
        // 持久化 + 转发
//        chatMessageService.saveAndForward(json);
    }

    private void forwardMessage(ChatMessageDTO dto , ChannelHandlerContext ctx) {
        // 转发消息（私聊）
        if (Objects.equals(dto.getChatType(), ChatTypeEnum.PRIVATE.getValue())) {
            Channel receiverChannel = ChannelManager.get(dto.getReceiverId());
            if (receiverChannel != null) {
                receiverChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(dto)));
            }
        }

        // 群聊广播
        if (Objects.equals(dto.getChatType(), ChatTypeEnum.GROUP.getValue())) {
            List<Long> memberList = clientUtils.getRoomUserIds(dto.getRoomId(), ctx);
            Set<Long> memberSet = new HashSet<>(memberList);
            for (Long memberId : memberSet) {
                if (memberId.equals(dto.getSenderId())) continue; // 不发给自己

                Channel ch = ChannelManager.get(memberId);
                if (ch != null) {
                    ch.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(WsResult.ok(ChatEventTypeEnum.CHAT.getValue(), dto))));
                }
            }
        }
    }

    private void safeRemove(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        ChannelId channelId = channel.id();

        // 获取并尝试移除 userId，如果已移除，则说明处理过
        Long userId = CHANNEL_USER_MAP.get(channelId);
        if (userId == null) {
            return; // 已经处理过了，幂等处理
        }

        // remove(k,v) 是线程安全的，只有当 key 对应 value 时才移除
        boolean removed = CHANNEL_USER_MAP.remove(channelId, userId);
        if (!removed) {
            return; // 别的线程已经处理了
        }

        try {
            ChannelManager.remove(channel);
            sessionManager.offline(userId);
        } catch (Exception e) {
            log.warn("safeRemove 出错：{}", e.getMessage(), e);
        }

        log.info("安全移除用户 {}，channel {}", userId, channelId.asLongText());
    }

    /**
     * 统一发送 WebSocket 响应
     */
    public static void sendSuccess(ChannelHandlerContext ctx, ChatEventTypeEnum type, Object data, String msg) {
        sendWsResult(ctx, WsResult.ok(type.getValue(), data, msg));
    }

    public static void sendFail(ChannelHandlerContext ctx, String message) {
        if (ctx.channel().isActive()) {
            sendWsResult(ctx, WsResult.fail(-1, message));
        }
    }

    public static void sendWsResult(ChannelHandlerContext ctx, WsResult<?> result) {
        String json = JSON.toJSONString(result);
        ctx.writeAndFlush(new TextWebSocketFrame(json));
    }

    public static void sendWsResult(Channel oldChannel, WsResult<?> result) {
        String json = JSON.toJSONString(result);
        oldChannel.writeAndFlush(new TextWebSocketFrame(json));
    }

}