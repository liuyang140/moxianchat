package com.ly.chatws.util;

import cn.hutool.core.collection.CollUtil;
import com.ly.chat.client.WebChatFeignClient;
import com.ly.common.constant.SystemConstant;
import com.ly.common.result.Result;
import com.ly.common.util.AuthContextHolder;
import com.ly.model.dto.chat.ChatMessageDTO;
import com.ly.model.dto.chat.UpdateMessageDTO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

@Component
@Slf4j
public class ClientUtils {

    @Autowired
    private WebChatFeignClient chatFeign;

    /*
     * 批量保存消息
     * */
    public Boolean saveMessages(UpdateMessageDTO uDto, ChannelHandlerContext ctx) {
        Result<List<ChatMessageDTO>> result = executeWithToken(
                () -> chatFeign.saveMessages(uDto),
                "保存消息",
                ctx
        );
        if (result != null && result.isOk()) {
            return true;
        }
        log.error("保存消息失败: {}", result != null ? result.getMessage() : "未知错误");
        return false;
    }

    /*
     * 批量撤回消息
     * */
    public List<ChatMessageDTO> reCallMessages(UpdateMessageDTO uDto,ChannelHandlerContext ctx) {
        Result<List<ChatMessageDTO>> result = executeWithToken(
                () -> chatFeign.recallMessages(uDto),
                "撤回消息"
                ,ctx
        );
        if (result != null && result.isOk()) {
            return result.getData();
        }
        log.error("撤回消息失败: {}", result != null ? result.getMessage() : "未知错误");
        return CollUtil.newArrayList();
    }

    /*
     * 根据房间id获取房间内所有用户id
     * */
    public List<Long> getRoomUserIds(Long roomId,ChannelHandlerContext ctx){
        Result<List<Long>> result = executeWithToken(
                () -> chatFeign.getRoomUserIds(roomId),
                "获取房间用户",
                ctx
        );
        if (result != null && result.isOk()) {
            return result.getData();
        }
        log.error("获取房间用户失败: {}", result != null ? result.getMessage() : "未知错误");
        return CollUtil.newArrayList();
    }

    /*
    * 模版方法
    * */
    public <T> T executeWithToken(Supplier<T> action, String errorMessage,ChannelHandlerContext ctx) {
        try {
            this.setHolder(ctx);
            return action.get();
        } catch (Exception e) {
            log.error("{}异常: {}", errorMessage, e.getMessage(), e);
            return null;
        } finally {
            AuthContextHolder.clearToken();
        }
    }

    public Boolean setHolder(ChannelHandlerContext ctx) {
        String token = (String) ctx.channel().attr(AttributeKey.valueOf(SystemConstant.TOKEN)).get();
        if (token != null) {
            AuthContextHolder.setToken(token); // 存入ThreadLocal
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}
